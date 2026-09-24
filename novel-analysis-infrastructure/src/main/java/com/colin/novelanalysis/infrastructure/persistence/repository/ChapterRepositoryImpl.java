package com.colin.novelanalysis.infrastructure.persistence.repository;

import com.colin.novelanalysis.domain.model.Chapter;
import com.colin.novelanalysis.domain.repository.ChapterRepository;
import com.colin.novelanalysis.infrastructure.persistence.mapper.ChapterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 章节仓储实现
 */
@Repository
@RequiredArgsConstructor
public class ChapterRepositoryImpl implements ChapterRepository {

    private final ChapterMapper chapterMapper;

    @Override
    public void saveBatch(List<Chapter> chapters) {
        for (Chapter chapter : chapters) {
            chapterMapper.insert(chapter);
        }
    }

    @Override
    public List<Chapter> findByNovelId(Long novelId) {
        return chapterMapper.selectByNovelId(novelId);
    }
}
