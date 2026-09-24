package com.colin.novelanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 章节
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chapter implements Entity {

    private Long id;

    private Long novelId;

    private Integer chapterNo;

    private String title;

    private Long startPos;

    private Long endPos;

    private LocalDateTime createdAt;
}
