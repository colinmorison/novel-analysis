package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.colin.novelanalysis.domain.model.Novel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 小说 Mapper
 */
@Mapper
public interface NovelMapper {

    int insert(Novel novel);

    Novel selectById(Long id);

    List<Novel> selectAll();

    int updateStatus(Novel novel);

    /**
     * CAS 更新状态
     */
    int updateStatusIf(@Param("id") Long id,
                       @Param("newStatus") String newStatus,
                       @Param("expectedStatus") String expectedStatus);
}
