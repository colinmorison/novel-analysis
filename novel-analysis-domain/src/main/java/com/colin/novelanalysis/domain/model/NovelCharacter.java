package com.colin.novelanalysis.domain.model;

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
public class NovelCharacter implements Entity {

    private Long id;

    private Long novelId;

    private String name;

    private String aliases;

    private String profile;

    private String appearChunkIds;

    private LocalDateTime createdAt;
}
