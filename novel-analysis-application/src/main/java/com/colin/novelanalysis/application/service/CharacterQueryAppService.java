package com.colin.novelanalysis.application.service;

import com.colin.novelanalysis.application.dto.CharacterTraceQueryCommand;
import com.colin.novelanalysis.application.dto.CharacterTraceResult;
import com.colin.novelanalysis.domain.model.KnowledgeChunk;
import com.colin.novelanalysis.domain.model.NovelEvent;
import com.colin.novelanalysis.domain.repository.KnowledgeRepository;
import com.colin.novelanalysis.domain.repository.NovelRepository;
import com.colin.novelanalysis.domain.service.AiChatService;
import com.colin.novelanalysis.domain.service.CharacterGraphService;
import com.colin.novelanalysis.domain.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 角色轨迹查询应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterQueryAppService {

    private final CharacterGraphService characterGraphService;
    private final KnowledgeRepository knowledgeRepository;
    private final EmbeddingService embeddingService;
    private final AiChatService aiChatService;
    private final NovelRepository novelRepository;

    private static final Pattern CHARACTER_PATTERN = Pattern.compile("([\\u4e00-\\u9fa5]{2,6})(?:的|发展|结局|轨迹|经历|一生|故事|怎么样)");

    public CharacterTraceResult trace(CharacterTraceQueryCommand command) {
        String characterName = extractCharacterName(command.getQuestion());
        if (StringUtils.isBlank(characterName)) {
            characterName = command.getQuestion().replaceAll("[^\\u4e00-\\u9fa5]", "").trim();
        }
        if (StringUtils.isBlank(characterName)) {
            return CharacterTraceResult.builder()
                    .sessionId(command.getSessionId())
                    .answer("未能从问题中识别出角色名，请换一种问法，例如：段誉的发展轨迹是什么样的？")
                    .build();
        }

        final String finalName = characterName;
        // 由于全局库，先查所有小说里的相关事件（取第一本有结果的小说）
        List<NovelEvent> events = List.of();
        Long novelId = null;
        var novels = novelRepository.findAll();
        for (var novel : novels) {
            List<NovelEvent> novelEvents = characterGraphService.findCharacterEvents(novel.getId(), finalName);
            if (!novelEvents.isEmpty()) {
                events = novelEvents;
                novelId = novel.getId();
                break;
            }
        }

        List<String> relatedCharacters = novelId != null
                ? characterGraphService.findRelatedCharacters(novelId, finalName)
                : List.of();

        String eventTimeline = buildEventTimeline(events);
        String chunksText = buildChunksText(novelId, finalName);

        String prompt = "你是一位熟读该小说的文学分析助手。请根据以下事件时间线与原文片段，概括角色“" + finalName + "”在小说中的发展轨迹及结局。\n\n" +
                "【事件时间线】\n" + eventTimeline + "\n\n" +
                "【相关原文片段】\n" + chunksText + "\n\n" +
                "要求：\n" +
                "1. 按时间顺序叙述；\n" +
                "2. 包含重要转折事件；\n" +
                "3. 最后说明角色结局；\n" +
                "4. 若原文未提及，请明确说明“原文未明确交代”。";

        String answer = aiChatService.chat(command.getSessionId(), prompt);

        return CharacterTraceResult.builder()
                .sessionId(command.getSessionId())
                .characterName(finalName)
                .answer(answer)
                .events(events.stream().map(e -> CharacterTraceResult.EventItem.builder()
                        .title(e.getTitle())
                        .summary(e.getSummary())
                        .chapterNo(e.getChapterNo())
                        .build()).toList())
                .relatedCharacters(relatedCharacters)
                .build();
    }

    private String extractCharacterName(String question) {
        Matcher matcher = CHARACTER_PATTERN.matcher(question);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String buildEventTimeline(List<NovelEvent> events) {
        if (events.isEmpty()) {
            return "（无明确事件记录）";
        }
        StringBuilder sb = new StringBuilder();
        for (NovelEvent event : events) {
            sb.append("第").append(event.getChapterNo()).append("章 ")
                    .append(event.getTitle()).append(": ")
                    .append(event.getSummary()).append("\n");
        }
        return sb.toString();
    }

    private String buildChunksText(Long novelId, String characterName) {
        if (novelId == null) {
            return "（无原文片段）";
        }
        float[] embedding = embeddingService.embed(characterName + " 发展轨迹");
        List<KnowledgeChunk> chunks = knowledgeRepository.searchSimilar(novelId, embedding, 5);
        StringBuilder sb = new StringBuilder();
        for (KnowledgeChunk chunk : chunks) {
            sb.append("[").append(chunk.getChapterTitle()).append("] ")
                    .append(chunk.getContent()).append("\n\n");
        }
        return sb.toString();
    }
}
