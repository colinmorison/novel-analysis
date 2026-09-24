package com.colin.novelanalysis.domain.service;

import com.colin.novelanalysis.domain.model.NovelCharacter;
import com.colin.novelanalysis.domain.model.NovelEvent;

import java.util.List;

/**
 * 角色图服务
 */
public interface CharacterGraphService {

    void saveNovel(Long novelId, String title);

    void saveCharacters(Long novelId, List<NovelCharacter> characters);

    void saveEvents(Long novelId, List<NovelEvent> events);

    List<NovelEvent> findCharacterEvents(Long novelId, String characterName);

    List<String> findRelatedCharacters(Long novelId, String characterName);

    void clearNovel(Long novelId);
}
