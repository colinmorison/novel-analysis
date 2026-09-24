package com.colin.novelanalysis.infrastructure.persistence.repository;

import com.colin.novelanalysis.domain.model.NovelCharacter;
import com.colin.novelanalysis.domain.repository.CharacterRepository;
import com.colin.novelanalysis.infrastructure.persistence.mapper.NovelCharacterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色仓储实现
 */
@Repository
@RequiredArgsConstructor
public class CharacterRepositoryImpl implements CharacterRepository {

    private final NovelCharacterMapper characterMapper;

    @Override
    public void saveBatch(List<NovelCharacter> characters) {
        for (NovelCharacter character : characters) {
            characterMapper.insert(character);
        }
    }

    @Override
    public List<NovelCharacter> findByNovelId(Long novelId) {
        return characterMapper.selectByNovelId(novelId);
    }

    @Override
    public List<NovelCharacter> findByNameLike(Long novelId, String name) {
        return characterMapper.selectByNameLike(novelId, "%" + name + "%");
    }
}
