package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.colin.novelanalysis.domain.model.NovelCharacter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper
 */
@Mapper
public interface NovelCharacterMapper {

    int insert(NovelCharacter character);

    List<NovelCharacter> selectByNovelId(Long novelId);

    List<NovelCharacter> selectByNameLike(@Param("novelId") Long novelId, @Param("name") String name);
}
