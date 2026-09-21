package com.colin.novelanalysis.interfaces.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户响应
 */
@Data
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private LocalDateTime createTime;
}
