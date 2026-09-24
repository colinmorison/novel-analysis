package com.colin.novelanalysis.interfaces.rest.controller;

import com.colin.novelanalysis.application.dto.ChatCommand;
import com.colin.novelanalysis.application.dto.ChatResult;
import com.colin.novelanalysis.application.service.ChatApplicationService;
import com.colin.novelanalysis.interfaces.rest.dto.ApiResult;
import com.colin.novelanalysis.interfaces.rest.dto.ChatRequest;
import com.colin.novelanalysis.interfaces.rest.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 聊天接口
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Validated
public class ChatController {

    private final ChatApplicationService chatApplicationService;

    @PostMapping
    public ApiResult<ChatResponse> chat(@RequestBody @Validated ChatRequest request) {
        ChatResult result = chatApplicationService.chat(ChatCommand.builder()
                .sessionId(request.getSessionId())
                .userMessage(request.getUserMessage())
                .build());
        return ApiResult.ok(ChatResponse.builder()
                .sessionId(result.getSessionId())
                .reply(result.getReply())
                .build());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String sessionId, @RequestParam String question) {
        SseEmitter emitter = new SseEmitter(0L);
        chatApplicationService.streamChat(
                ChatCommand.builder().sessionId(sessionId).userMessage(question).build(),
                token -> {
                    try {
                        emitter.send(SseEmitter.event().data(token));
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                },
                emitter::complete);
        return emitter;
    }
}
