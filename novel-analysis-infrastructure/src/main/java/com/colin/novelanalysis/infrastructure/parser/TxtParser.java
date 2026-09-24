package com.colin.novelanalysis.infrastructure.parser;

import com.colin.novelanalysis.domain.model.Novel;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * TXT 小说解析器
 */
@Component
public class TxtParser extends AbstractNovelParser {

    @Override
    public boolean supports(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".txt");
    }

    @Override
    public List<ChapterContent> parse(Novel novel, InputStream inputStream) {
        try {
            String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return splitChapters(text);
        } catch (Exception e) {
            throw new RuntimeException("TXT 解析失败", e);
        }
    }
}
