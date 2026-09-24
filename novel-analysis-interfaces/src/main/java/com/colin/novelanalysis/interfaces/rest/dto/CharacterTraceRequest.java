package com.colin.novelanalysis.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 角色轨迹请求
 */
@Data
public class CharacterTraceRequest {

    @NotBlank(message = "会话 ID 不能为空")
    private String sessionId;

    @NotBlank(message = "问题不能为空")
    private String question;
}
