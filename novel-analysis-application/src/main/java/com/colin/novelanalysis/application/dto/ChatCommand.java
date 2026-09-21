package com.colin.novelanalysis.application.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 聊天命令
 */
@Data
@Builder
public class ChatCommand {

    @NotBlank(message = "会话 ID 不能为空")
    private String sessionId;

    @NotBlank(message = "用户消息不能为空")
    private String userMessage;
}
