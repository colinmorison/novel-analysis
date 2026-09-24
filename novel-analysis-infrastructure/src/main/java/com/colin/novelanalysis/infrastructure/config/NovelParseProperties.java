package com.colin.novelanalysis.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 小说解析配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "novel.parse")
public class NovelParseProperties {

    private int chunkSize = 500;

    private int chunkOverlap = 100;

    private long maxFileSizeMb = 50;
}
