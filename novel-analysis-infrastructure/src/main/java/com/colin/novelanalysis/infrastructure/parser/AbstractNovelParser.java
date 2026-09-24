package com.colin.novelanalysis.infrastructure.parser;

import com.colin.novelanalysis.domain.model.Novel;
import com.colin.novelanalysis.domain.service.NovelParser;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 小说解析器抽象类
 */
public abstract class AbstractNovelParser implements NovelParser {

    // 匹配“第X章”、“第X回”、“Chapter X”等
    private static final Pattern CHAPTER_PATTERN = Pattern.compile(
            "(\\s|^)(第[一二三四五六七八九十百千万零\\d]+章|第[一二三四五六七八九十百千万零\\d]+回|Chapter\\s+\\d+|CHAPTER\\s+\\d+)(\\s|：|:|\\n)");

    protected List<ChapterContent> splitChapters(String fullText) {
        List<ChapterContent> chapters = new ArrayList<>();
        Matcher matcher = CHAPTER_PATTERN.matcher(fullText);

        int lastEnd = 0;
        String lastTitle = "前言";
        int chapterNo = 0;
        long startPos = 0;

        while (matcher.find()) {
            if (lastEnd > 0) {
                String content = fullText.substring(lastEnd, matcher.start()).trim();
                if (StringUtils.isNotBlank(content)) {
                    chapters.add(new ChapterContent(chapterNo, lastTitle, content, startPos, (long) lastEnd));
                }
            }
            chapterNo++;
            lastTitle = matcher.group(2).trim();
            lastEnd = matcher.end();
            startPos = matcher.start();
        }

        if (lastEnd < fullText.length()) {
            String content = fullText.substring(lastEnd).trim();
            if (StringUtils.isNotBlank(content)) {
                chapters.add(new ChapterContent(chapterNo, lastTitle, content, startPos, (long) fullText.length()));
            }
        }

        // 如果没有匹配到章节，整本作为一个章节
        if (chapters.isEmpty()) {
            chapters.add(new ChapterContent(1, "全文", fullText.trim(), 0L, (long) fullText.length()));
        }

        return chapters;
    }
}
