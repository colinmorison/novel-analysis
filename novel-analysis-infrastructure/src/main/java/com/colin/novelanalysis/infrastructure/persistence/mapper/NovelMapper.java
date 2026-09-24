package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.colin.novelanalysis.domain.model.Novel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 小说 Mapper
 */
@Mapper
public interface NovelMapper extends BaseMapper<Novel> {

    @Update("UPDATE novel SET parse_status = #{parseStatus}, parse_error = #{parseError}, parsed_at = #{parsedAt} WHERE id = #{id}")
    int updateStatus(Novel novel);
}
