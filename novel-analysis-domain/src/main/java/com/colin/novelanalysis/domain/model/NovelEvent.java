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
 * 小说事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("novel_event")
public class NovelEvent implements Entity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long novelId;

    private String title;

    private String summary;

    private String characters;

    private Integer chapterNo;

    private Integer orderInChapter;

    private LocalDateTime createdAt;
}
