package com.colin.novelanalysis.infrastructure.parser;

import com.colin.novelanalysis.infrastructure.config.NovelParseProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本分块器
 */
@Component
@RequiredArgsConstructor
public class TextChunker {

    private final NovelParseProperties properties;

    public List<String> chunk(String text) {
        int size = properties.getChunkSize();
        int overlap = properties.getChunkOverlap();
        List<String> chunks = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return chunks;
        }
        // 按句子粗略切分（简单按标点）
        String[] sentences = text.split("([。！？\\.!?\\n]+)");
        StringBuilder current = new StringBuilder();
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty()) {
                continue;
            }
            if (current.length() + sentence.length() > size && current.length() > 0) {
                chunks.add(current.toString().trim());
                String prev = current.toString();
                current.setLength(0);
                if (overlap > 0 && prev.length() > overlap) {
                    current.append(prev.substring(prev.length() - overlap));
                }
            }
            current.append(sentence).append("。");
        }
        if (current.length() > 0) {
            chunks.add(current.toString().trim());
        }
        return chunks;
    }
}
