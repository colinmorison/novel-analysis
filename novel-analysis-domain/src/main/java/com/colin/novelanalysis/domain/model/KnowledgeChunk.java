package com.colin.novelanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识片段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeChunk implements Entity {

    private Long id;

    private Long novelId;

    private Long chapterId;

    private Integer chapterNo;

    private String chapterTitle;

    private Integer chunkNo;

    private String content;

    private float[] embedding;

    private Integer tokenCount;

    private LocalDateTime createdAt;
}
