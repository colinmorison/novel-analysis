package com.colin.novelanalysis.application.service;

import com.colin.novelanalysis.application.dto.ChatCommand;
import com.colin.novelanalysis.application.dto.ChatResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;

import jakarta.validation.Valid;
import java.util.function.Consumer;

/**
 * 聊天应用服务（兼容旧接口，委托给 ChatAppService）
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ChatApplicationService {

    private final ChatAppService chatAppService;

    public ChatResult chat(@Valid ChatCommand command) {
        return chatAppService.chat(command);
    }

    public void streamChat(@Valid ChatCommand command, Consumer<String> onChunk, Runnable onComplete) {
        chatAppService.streamChat(command, onChunk, onComplete);
    }

    public Flux<String> streamChat(@Valid ChatCommand command) {
        return chatAppService.streamChat(command);
    }
}
