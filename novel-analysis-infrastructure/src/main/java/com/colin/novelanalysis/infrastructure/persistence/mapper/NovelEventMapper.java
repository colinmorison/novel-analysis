package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.colin.novelanalysis.domain.model.NovelEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 事件 Mapper
 */
@Mapper
public interface NovelEventMapper {

    int insert(NovelEvent event);

    List<NovelEvent> selectByNovelId(Long novelId);

    List<NovelEvent> selectByNovelIdAndCharacter(@Param("novelId") Long novelId, @Param("characterName") String characterName);
}
