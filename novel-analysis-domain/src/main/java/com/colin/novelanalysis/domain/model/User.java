package com.colin.novelanalysis.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 示例用户领域实体
 */
@Data
@Builder
public class User implements Entity {

    private Long id;

    private String username;

    private String email;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
