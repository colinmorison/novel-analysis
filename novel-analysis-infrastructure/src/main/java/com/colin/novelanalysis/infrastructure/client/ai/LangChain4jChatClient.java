package com.colin.novelanalysis.infrastructure.client.ai;

import com.colin.novelanalysis.domain.service.AiChatService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * 基于 LangChain4j 的对话客户端实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LangChain4jChatClient implements AiChatService {

    private final ChatLanguageModel chatLanguageModel;
    private final StreamingChatLanguageModel streamingChatLanguageModel;

    @Override
    public String chat(String sessionId, String userMessage) {
        log.debug("[LangChain4j] session={}", sessionId);
        return chatLanguageModel.generate(userMessage);
    }

    @Override
    public void streamChat(String sessionId, String userMessage, Consumer<String> onChunk) {
        log.debug("[LangChain4j] stream session={}", sessionId);
        streamingChatLanguageModel.generate(userMessage, new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                onChunk.accept(token);
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                // ignore
            }

            @Override
            public void onError(Throwable error) {
                log.error("[LangChain4j] stream error", error);
            }
        });
    }
}
