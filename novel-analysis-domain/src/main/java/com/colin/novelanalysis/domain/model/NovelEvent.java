package com.colin.novelanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 小说事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelEvent implements Entity {

    private Long id;

    private Long novelId;

    private String title;

    private String summary;

    private String characters;

    private Integer chapterNo;

    private Integer orderInChapter;

    private LocalDateTime createdAt;
}
