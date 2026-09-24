package com.colin.novelanalysis.infrastructure.persistence.repository;

import com.colin.novelanalysis.domain.model.NovelEvent;
import com.colin.novelanalysis.domain.repository.EventRepository;
import com.colin.novelanalysis.infrastructure.persistence.mapper.NovelEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 事件仓储实现
 */
@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final NovelEventMapper eventMapper;

    @Override
    public void saveBatch(List<NovelEvent> events) {
        for (NovelEvent event : events) {
            eventMapper.insert(event);
        }
    }

    @Override
    public List<NovelEvent> findByNovelId(Long novelId) {
        return eventMapper.selectByNovelId(novelId);
    }

    @Override
    public List<NovelEvent> findByNovelIdAndCharacter(Long novelId, String characterName) {
        return eventMapper.selectByNovelIdAndCharacter(novelId, "%" + characterName + "%");
    }
}
