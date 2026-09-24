package com.colin.novelanalysis.domain.repository;

import com.colin.novelanalysis.domain.model.Chapter;

import java.util.List;

/**
 * 章节仓储
 */
public interface ChapterRepository {

    void saveBatch(List<Chapter> chapters);

    List<Chapter> findByNovelId(Long novelId);
}
