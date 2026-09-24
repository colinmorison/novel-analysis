package com.colin.novelanalysis.domain.repository;

import com.colin.novelanalysis.domain.model.KnowledgeChunk;

import java.util.List;

/**
 * 知识片段仓储
 */
public interface KnowledgeRepository {

    void saveBatch(List<KnowledgeChunk> chunks);

    List<KnowledgeChunk> findByNovelId(Long novelId);

    List<KnowledgeChunk> searchSimilar(Long novelId, float[] embedding, int topK);

    void deleteByNovelId(Long novelId);
}
