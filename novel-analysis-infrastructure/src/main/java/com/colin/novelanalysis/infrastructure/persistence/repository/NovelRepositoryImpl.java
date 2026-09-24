package com.colin.novelanalysis.infrastructure.persistence.repository;

import com.colin.novelanalysis.domain.model.Novel;
import com.colin.novelanalysis.domain.repository.NovelRepository;
import com.colin.novelanalysis.infrastructure.persistence.mapper.NovelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 小说仓储实现
 */
@Repository
@RequiredArgsConstructor
public class NovelRepositoryImpl implements NovelRepository {

    private final NovelMapper novelMapper;

    @Override
    public Novel save(Novel novel) {
        novelMapper.insert(novel);
        return novel;
    }

    @Override
    public Novel findById(Long id) {
        return novelMapper.selectById(id);
    }

    @Override
    public List<Novel> findAll() {
        return novelMapper.selectAll();
    }

    @Override
    public void updateStatus(Novel novel) {
        novelMapper.updateStatus(novel);
    }
}
