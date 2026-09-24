package com.colin.novelanalysis.infrastructure.service;

import com.colin.novelanalysis.domain.model.*;
import com.colin.novelanalysis.domain.repository.*;
import com.colin.novelanalysis.domain.service.*;
import com.colin.novelanalysis.infrastructure.parser.NovelParserRouter;
import com.colin.novelanalysis.infrastructure.parser.TextChunker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 小说解析领域服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovelParseServiceImpl implements NovelParseService {

    private final NovelRepository novelRepository;
    private final ChapterRepository chapterRepository;
    private final KnowledgeRepository knowledgeRepository;
    private final CharacterRepository characterRepository;
    private final EventRepository eventRepository;
    private final CharacterGraphService characterGraphService;
    private final FileStorageService fileStorageService;
    private final EmbeddingService embeddingService;
    private final AiChatService aiChatService;
    private final NovelParserRouter parserRouter;
    private final TextChunker textChunker;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void parse(Long novelId) {
        Novel novel = novelRepository.findById(novelId);
        if (novel == null) {
            log.warn("[parse] novel not found, id={}", novelId);
            return;
        }

        try {
            log.info("[parse] start novelId={}", novelId);
            novel.startParsing();
            novelRepository.updateStatus(novel);

            List<NovelParser.ChapterContent> contents;
            try (InputStream is = fileStorageService.download(novel.getMinioPath())) {
                contents = parserRouter.route(novel.getFileName()).parse(novel, is);
            }

            // 保存章节
            List<Chapter> chapters = saveChapters(novelId, contents);

            // 分块、向量化、保存
            List<KnowledgeChunk> allChunks = new ArrayList<>();
            for (int i = 0; i < contents.size(); i++) {
                NovelParser.ChapterContent content = contents.get(i);
                Chapter chapter = chapters.get(i);
                List<String> chunks = textChunker.chunk(content.content());
                for (int j = 0; j < chunks.size(); j++) {
                    String chunkText = chunks.get(j);
                    allChunks.add(KnowledgeChunk.builder()
                            .novelId(novelId)
                            .chapterId(chapter.getId())
                            .chapterNo(content.chapterNo())
                            .chapterTitle(content.title())
                            .chunkNo(j + 1)
                            .content(chunkText)
                            .tokenCount(chunkText.length())
                            .createdAt(LocalDateTime.now())
                            .build());
                }
            }

            // 批量 Embedding
            embedChunks(allChunks);
            knowledgeRepository.saveBatch(allChunks);

            // 抽取角色与事件
            extractCharactersAndEvents(novel, contents);

            novel.complete();
            novelRepository.updateStatus(novel);
            log.info("[parse] completed novelId={}, chunks={}", novelId, allChunks.size());
        } catch (Exception e) {
            log.error("[parse] failed novelId={}", novelId, e);
            novel.fail(StringUtils.abbreviate(e.getMessage(), 500));
            novelRepository.updateStatus(novel);
        }
    }

    private List<Chapter> saveChapters(Long novelId, List<NovelParser.ChapterContent> contents) {
        List<Chapter> chapters = new ArrayList<>();
        for (NovelParser.ChapterContent content : contents) {
            chapters.add(Chapter.builder()
                    .novelId(novelId)
                    .chapterNo(content.chapterNo())
                    .title(content.title())
                    .startPos(content.startPos())
                    .endPos(content.endPos())
                    .createdAt(LocalDateTime.now())
                    .build());
        }
        chapterRepository.saveBatch(chapters);
        return chapters;
    }

    private void embedChunks(List<KnowledgeChunk> chunks) {
        List<String> texts = chunks.stream()
                .map(c -> "[" + c.getChapterTitle() + "] " + c.getContent())
                .toList();
        List<float[]> embeddings = embeddingService.embed(texts);
        for (int i = 0; i < chunks.size(); i++) {
            chunks.get(i).setEmbedding(embeddings.get(i));
        }
    }

    private void extractCharactersAndEvents(Novel novel, List<NovelParser.ChapterContent> contents) {
        String sample = buildSampleText(contents);

        String characterPrompt = "请从以下小说文本中抽取主要角色列表。每个角色包含：name（角色名）、aliases（别名数组，没有则空数组）、profile（一句话简介）。\n" +
                "只返回 JSON 数组，不要解释。\n\n" + sample;
        String characterJson = aiChatService.chat("extract_" + novel.getId(), characterPrompt);
        List<NovelCharacter> characters = parseCharacters(novel.getId(), characterJson);
        characterRepository.saveBatch(characters);
        characterGraphService.saveCharacters(novel.getId(), characters);

        String eventPrompt = "请从以下小说文本中抽取与角色相关的主要事件。每个事件包含：title（事件标题）、summary（事件摘要）、characters（参与角色名数组）、chapterNo（发生章节序号，未知填0）。\n" +
                "只返回 JSON 数组，不要解释。\n\n" + sample;
        String eventJson = aiChatService.chat("extract_" + novel.getId(), eventPrompt);
        List<NovelEvent> events = parseEvents(novel.getId(), eventJson);
        eventRepository.saveBatch(events);
        characterGraphService.saveEvents(novel.getId(), events);
    }

    private String buildSampleText(List<NovelParser.ChapterContent> contents) {
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(contents.size(), 30);
        for (int i = 0; i < limit; i++) {
            NovelParser.ChapterContent content = contents.get(i);
            sb.append("\n【").append(content.title()).append("】\n");
            sb.append(StringUtils.abbreviate(content.content(), 1500));
        }
        return sb.toString();
    }

    @SneakyThrows
    private List<NovelCharacter> parseCharacters(Long novelId, String json) {
        String cleaned = cleanJson(json);
        List<CharacterExtract> list = objectMapper.readValue(cleaned, new TypeReference<>() {
        });
        return list.stream().map(c -> NovelCharacter.builder()
                .novelId(novelId)
                .name(c.name())
                .aliases(toJson(c.aliases()))
                .profile(c.profile())
                .createdAt(LocalDateTime.now())
                .build()).toList();
    }

    @SneakyThrows
    private List<NovelEvent> parseEvents(Long novelId, String json) {
        String cleaned = cleanJson(json);
        List<EventExtract> list = objectMapper.readValue(cleaned, new TypeReference<>() {
        });
        return list.stream().map(e -> NovelEvent.builder()
                .novelId(novelId)
                .title(e.title())
                .summary(e.summary())
                .characters(toJson(e.characters()))
                .chapterNo(e.chapterNo() == null ? 0 : e.chapterNo())
                .orderInChapter(0)
                .createdAt(LocalDateTime.now())
                .build()).toList();
    }

    private String cleanJson(String json) {
        if (json == null) {
            return "[]";
        }
        String trimmed = json.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        }
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        trimmed = trimmed.trim();
        if (trimmed.isEmpty()) {
            return "[]";
        }
        return trimmed;
    }

    @SneakyThrows
    private String toJson(List<String> list) {
        if (list == null) {
            return "[]";
        }
        return objectMapper.writeValueAsString(list);
    }

    private record CharacterExtract(String name, List<String> aliases, String profile) {
    }

    private record EventExtract(String title, String summary, List<String> characters, Integer chapterNo) {
    }
}
