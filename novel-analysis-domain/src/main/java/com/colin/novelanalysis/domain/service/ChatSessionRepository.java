package com.colin.novelanalysis.domain.service;

import com.colin.novelanalysis.domain.model.ChatMessage;

import java.util.List;

/**
 * 会话仓储接口
 */
public interface ChatSessionRepository {

    void addMessage(String sessionId, ChatMessage message);

    List<ChatMessage> getHistory(String sessionId);

    void clear(String sessionId);
}
