package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.colin.novelanalysis.domain.model.NovelCharacter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色 Mapper
 */
@Mapper
public interface NovelCharacterMapper extends BaseMapper<NovelCharacter> {

    @Select("SELECT * FROM novel_character WHERE novel_id = #{novelId}")
    List<NovelCharacter> selectByNovelId(Long novelId);

    @Select("SELECT * FROM novel_character WHERE novel_id = #{novelId} AND name ILIKE #{name}")
    List<NovelCharacter> selectByNameLike(@Param("novelId") Long novelId, @Param("name") String name);
}
