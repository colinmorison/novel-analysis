package com.colin.novelanalysis.application.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 聊天结果
 */
@Data
@Builder
public class ChatResult {

    private String sessionId;
    private String reply;
}
