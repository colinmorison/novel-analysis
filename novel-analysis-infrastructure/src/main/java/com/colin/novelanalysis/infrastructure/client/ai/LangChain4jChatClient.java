package com.colin.novelanalysis.infrastructure.client.ai;

import com.colin.novelanalysis.domain.service.AiChatService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 基于 LangChain4j 的对话客户端实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LangChain4jChatClient implements AiChatService {

    private final ChatLanguageModel chatLanguageModel;

    @Override
    public String chat(String sessionId, String userMessage) {
        log.debug("[LangChain4j] session={}", sessionId);
        return chatLanguageModel.generate(userMessage);
    }
}
