package com.colin.novelanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 小说聚合根
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Novel implements AggregateRoot {

    private Long id;

    private String title;

    private String author;

    private String fileName;

    private String minioPath;

    private Long fileSize;

    private String parseStatus;

    private String parseError;

    private LocalDateTime uploadedAt;

    private LocalDateTime parsedAt;

    public void startParsing() {
        this.parseStatus = ParseStatus.PARSING.name();
    }

    public void complete() {
        this.parseStatus = ParseStatus.COMPLETED.name();
        this.parsedAt = LocalDateTime.now();
    }

    public void fail(String error) {
        this.parseStatus = ParseStatus.FAILED.name();
        this.parseError = error;
    }
}
