package com.colin.novelanalysis.domain.repository;

import com.colin.novelanalysis.domain.model.NovelCharacter;

import java.util.List;

/**
 * 角色仓储
 */
public interface CharacterRepository {

    void saveBatch(List<NovelCharacter> characters);

    List<NovelCharacter> findByNovelId(Long novelId);

    List<NovelCharacter> findByNameLike(Long novelId, String name);
}
