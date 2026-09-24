package com.colin.novelanalysis.infrastructure.graph;

import com.colin.novelanalysis.domain.model.NovelCharacter;
import com.colin.novelanalysis.domain.model.NovelEvent;
import com.colin.novelanalysis.domain.service.CharacterGraphService;
import com.colin.novelanalysis.infrastructure.graph.entity.*;
import com.colin.novelanalysis.infrastructure.graph.repository.CharacterNodeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Neo4j 角色图服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Neo4jCharacterGraphService implements CharacterGraphService {

    private final CharacterNodeRepository characterNodeRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveNovel(Long novelId, String title) {
        // 小说节点在保存角色/事件时通过关系隐式创建即可
        log.info("[Neo4j] save novel novelId={} title={}", novelId, title);
    }

    @Override
    public void saveCharacters(Long novelId, List<NovelCharacter> characters) {
        for (NovelCharacter c : characters) {
            CharacterNode node = CharacterNode.builder()
                    .id("c_" + novelId + "_" + c.getName())
                    .novelId(novelId)
                    .name(c.getName())
                    .aliases(parseJsonList(c.getAliases()))
                    .profile(c.getProfile())
                    .novel(NovelNode.builder().novelId(novelId).build())
                    .build();
            characterNodeRepository.save(node);
        }
    }

    @Override
    public void saveEvents(Long novelId, List<NovelEvent> events) {
        for (NovelEvent e : events) {
            List<String> characters = parseJsonList(e.getCharacters());
            EventNode eventNode = EventNode.builder()
                    .id("e_" + novelId + "_" + UUID.randomUUID())
                    .novelId(novelId)
                    .title(e.getTitle())
                    .summary(e.getSummary())
                    .chapterNo(e.getChapterNo())
                    .orderInChapter(e.getOrderInChapter())
                    .novel(NovelNode.builder().novelId(novelId).build())
                    .build();

            for (String name : characters) {
                characterNodeRepository.findByNovelIdAndName(novelId, name)
                        .ifPresent(characterNode -> {
                            characterNode.getEvents().add(eventNode);
                            characterNodeRepository.save(characterNode);
                        });
            }
        }
    }

    @Override
    public List<NovelEvent> findCharacterEvents(Long novelId, String characterName) {
        List<EventNode> eventNodes = characterNodeRepository.findEventsByCharacter(novelId, characterName);
        return eventNodes.stream().map(this::toNovelEvent).toList();
    }

    @Override
    public List<String> findRelatedCharacters(Long novelId, String characterName) {
        return characterNodeRepository.findRelatedCharacterNames(novelId, characterName);
    }

    @Override
    public void clearNovel(Long novelId) {
        List<CharacterNode> characters = characterNodeRepository.findByNovelId(novelId);
        characterNodeRepository.deleteAll(characters);
    }

    private NovelEvent toNovelEvent(EventNode node) {
        return NovelEvent.builder()
                .novelId(node.getNovelId())
                .title(node.getTitle())
                .summary(node.getSummary())
                .chapterNo(node.getChapterNo())
                .orderInChapter(node.getOrderInChapter())
                .build();
    }

    @SneakyThrows
    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }
}
