package com.colin.novelanalysis.domain.repository;

import com.colin.novelanalysis.domain.model.Novel;

import java.util.List;

/**
 * 小说仓储
 */
public interface NovelRepository {

    Novel save(Novel novel);

    Novel findById(Long id);

    List<Novel> findAll();

    void updateStatus(Novel novel);
}
