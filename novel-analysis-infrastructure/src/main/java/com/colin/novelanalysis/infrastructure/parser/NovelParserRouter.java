package com.colin.novelanalysis.infrastructure.parser;

import com.colin.novelanalysis.domain.service.NovelParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 小说解析器路由
 */
@Component
@RequiredArgsConstructor
public class NovelParserRouter {

    private final List<NovelParser> parsers;

    public NovelParser route(String fileName) {
        return parsers.stream()
                .filter(p -> p.supports(fileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的文件格式: " + fileName));
    }
}
