package com.colin.novelanalysis.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 DTO
 */
@Data
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private LocalDateTime createTime;
}
