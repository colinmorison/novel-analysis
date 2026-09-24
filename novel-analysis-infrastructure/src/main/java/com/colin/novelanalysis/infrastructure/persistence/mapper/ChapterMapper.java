package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.colin.novelanalysis.domain.model.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 章节 Mapper
 */
@Mapper
public interface ChapterMapper extends BaseMapper<Chapter> {

    @Select("SELECT * FROM novel_chapter WHERE novel_id = #{novelId} ORDER BY chapter_no")
    List<Chapter> selectByNovelId(Long novelId);
}
