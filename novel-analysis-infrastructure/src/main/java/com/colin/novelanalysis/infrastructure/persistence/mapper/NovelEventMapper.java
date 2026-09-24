package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.colin.novelanalysis.domain.model.NovelEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 事件 Mapper
 */
@Mapper
public interface NovelEventMapper extends BaseMapper<NovelEvent> {

    @Select("SELECT * FROM novel_event WHERE novel_id = #{novelId} ORDER BY chapter_no, order_in_chapter")
    List<NovelEvent> selectByNovelId(Long novelId);

    @Select("SELECT * FROM novel_event WHERE novel_id = #{novelId} AND characters ILIKE #{characterName} ORDER BY chapter_no, order_in_chapter")
    List<NovelEvent> selectByNovelIdAndCharacter(@Param("novelId") Long novelId, @Param("characterName") String characterName);
}
