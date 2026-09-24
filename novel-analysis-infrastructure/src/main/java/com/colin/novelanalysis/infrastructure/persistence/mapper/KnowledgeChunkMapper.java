package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.colin.novelanalysis.domain.model.KnowledgeChunk;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识片段 Mapper
 */
@Mapper
public interface KnowledgeChunkMapper extends BaseMapper<KnowledgeChunk> {

    int insertWithEmbedding(KnowledgeChunk chunk);

    List<KnowledgeChunk> selectByNovelId(Long novelId);

    List<KnowledgeChunk> searchSimilar(@Param("novelId") Long novelId,
                                       @Param("embedding") float[] embedding,
                                       @Param("topK") int topK);

    void deleteByNovelId(Long novelId);
}
