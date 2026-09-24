package com.colin.novelanalysis.domain.service;

import java.util.function.Consumer;

/**
 * AI 对话领域服务接口
 */
public interface AiChatService {

    /**
     * 单次对话
     *
     * @param sessionId   会话 ID
     * @param userMessage 用户输入
     * @return AI 回复
     */
    String chat(String sessionId, String userMessage);

    /**
     * 流式对话
     *
     * @param sessionId   会话 ID
     * @param userMessage 用户输入
     * @param onChunk     每段回调
     */
    void streamChat(String sessionId, String userMessage, Consumer<String> onChunk);
}
