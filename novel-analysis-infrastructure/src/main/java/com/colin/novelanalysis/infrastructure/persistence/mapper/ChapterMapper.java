package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.colin.novelanalysis.domain.model.Chapter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 章节 Mapper
 */
@Mapper
public interface ChapterMapper {

    int insert(Chapter chapter);

    List<Chapter> selectByNovelId(Long novelId);
}
