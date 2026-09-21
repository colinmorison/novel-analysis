package com.colin.novelanalysis.interfaces.rest.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 聊天响应 DTO
 */
@Data
@Builder
public class ChatResponse {

    private String sessionId;
    private String reply;
}
