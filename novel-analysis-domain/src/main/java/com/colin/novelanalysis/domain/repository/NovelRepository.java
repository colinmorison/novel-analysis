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

    /**
     * CAS 更新状态，仅当当前状态为 expectedStatus 时才更新为 newStatus
     * @return 实际更新的行数
     */
    int updateStatusIf(Long id, String newStatus, String expectedStatus);
}
