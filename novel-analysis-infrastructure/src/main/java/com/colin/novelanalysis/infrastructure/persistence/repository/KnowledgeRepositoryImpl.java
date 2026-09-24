package com.colin.novelanalysis.infrastructure.persistence.repository;

import com.colin.novelanalysis.domain.model.KnowledgeChunk;
import com.colin.novelanalysis.domain.repository.KnowledgeRepository;
import com.colin.novelanalysis.infrastructure.persistence.mapper.KnowledgeChunkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 知识片段仓储实现
 */
@Repository
@RequiredArgsConstructor
public class KnowledgeRepositoryImpl implements KnowledgeRepository {

    private final KnowledgeChunkMapper knowledgeChunkMapper;

    @Override
    public void saveBatch(List<KnowledgeChunk> chunks) {
        for (KnowledgeChunk chunk : chunks) {
            knowledgeChunkMapper.insertWithEmbedding(chunk);
        }
    }

    @Override
    public List<KnowledgeChunk> findByNovelId(Long novelId) {
        return knowledgeChunkMapper.selectByNovelId(novelId);
    }

    @Override
    public List<KnowledgeChunk> searchSimilar(Long novelId, float[] embedding, int topK) {
        return knowledgeChunkMapper.searchSimilar(novelId, embedding, topK);
    }

    @Override
    public void deleteByNovelId(Long novelId) {
        knowledgeChunkMapper.deleteByNovelId(novelId);
    }
}
