package com.colin.novelanalysis.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("novel_chapter")
public class Chapter implements Entity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long novelId;

    private Integer chapterNo;

    private String title;

    private Long startPos;

    private Long endPos;

    private LocalDateTime createdAt;
}
