package com.colin.novelanalysis.domain.repository;

import com.colin.novelanalysis.domain.model.NovelEvent;

import java.util.List;

/**
 * 事件仓储
 */
public interface EventRepository {

    void saveBatch(List<NovelEvent> events);

    List<NovelEvent> findByNovelId(Long novelId);

    List<NovelEvent> findByNovelIdAndCharacter(Long novelId, String characterName);
}
