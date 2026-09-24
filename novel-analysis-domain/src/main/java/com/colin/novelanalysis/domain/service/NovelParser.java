package com.colin.novelanalysis.domain.service;

import com.colin.novelanalysis.domain.model.Novel;

import java.io.InputStream;
import java.util.List;

/**
 * 小说解析器
 */
public interface NovelParser {

    boolean supports(String fileName);

    List<ChapterContent> parse(Novel novel, InputStream inputStream);

    /**
     * 章节内容
     */
    record ChapterContent(Integer chapterNo, String title, String content, Long startPos, Long endPos) {
    }
}
