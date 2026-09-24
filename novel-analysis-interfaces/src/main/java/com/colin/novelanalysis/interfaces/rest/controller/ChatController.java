package com.colin.novelanalysis.interfaces.rest.controller;

import com.colin.novelanalysis.application.dto.ChatCommand;
import com.colin.novelanalysis.application.dto.ChatResult;
import com.colin.novelanalysis.application.service.ChatApplicationService;
import com.colin.novelanalysis.interfaces.rest.dto.ApiResult;
import com.colin.novelanalysis.interfaces.rest.dto.ChatRequest;
import com.colin.novelanalysis.interfaces.rest.dto.ChatResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 聊天接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Validated
public class ChatController {

    private final ChatApplicationService chatApplicationService;

    @PostMapping
    public Mono<ApiResult<ChatResponse>> chat(@RequestBody @Valid ChatRequest request) {
        return Mono.fromCallable(() -> {
            ChatResult result = chatApplicationService.chat(ChatCommand.builder()
                    .sessionId(request.getSessionId())
                    .userMessage(request.getUserMessage())
                    .build());
            return ApiResult.ok(ChatResponse.builder()
                    .sessionId(result.getSessionId())
                    .reply(result.getReply())
                    .build());
        });
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String sessionId, @RequestParam String question) {
        return chatApplicationService.streamChat(ChatCommand.builder()
                .sessionId(sessionId)
                .userMessage(question)
                .build());
    }
}
