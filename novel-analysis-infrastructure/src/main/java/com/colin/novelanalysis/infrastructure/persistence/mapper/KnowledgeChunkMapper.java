package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.colin.novelanalysis.domain.model.KnowledgeChunk;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识片段 Mapper
 */
@Mapper
public interface KnowledgeChunkMapper {

    int insert(KnowledgeChunk chunk);

    List<KnowledgeChunk> selectByNovelId(Long novelId);

    List<KnowledgeChunk> searchSimilar(@Param("novelId") Long novelId,
                                       @Param("embedding") float[] embedding,
                                       @Param("topK") int topK);

    int deleteByNovelId(Long novelId);
}
