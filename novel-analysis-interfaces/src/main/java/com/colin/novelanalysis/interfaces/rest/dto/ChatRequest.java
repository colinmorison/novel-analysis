package com.colin.novelanalysis.interfaces.rest.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 聊天请求 DTO
 */
@Data
public class ChatRequest {

    @NotBlank(message = "会话 ID 不能为空")
    private String sessionId;

    @NotBlank(message = "用户消息不能为空")
    private String userMessage;
}
