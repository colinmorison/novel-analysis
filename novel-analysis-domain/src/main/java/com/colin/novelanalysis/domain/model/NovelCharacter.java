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
 * 小说角色
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("novel_character")
public class NovelCharacter implements Entity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long novelId;

    private String name;

    private String aliases;

    private String profile;

    private String appearChunkIds;

    private LocalDateTime createdAt;
}
