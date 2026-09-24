package com.colin.novelanalysis.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("knowledge_chunk")
public class KnowledgeChunk implements Entity {

    @TableId(type = IdType.AUTO)
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
