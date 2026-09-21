package com.colin.novelanalysis.interfaces.rest.controller;

import com.colin.novelanalysis.application.dto.ChatCommand;
import com.colin.novelanalysis.application.dto.ChatResult;
import com.colin.novelanalysis.application.service.ChatApplicationService;
import com.colin.novelanalysis.interfaces.rest.dto.ApiResult;
import com.colin.novelanalysis.interfaces.rest.dto.ChatRequest;
import com.colin.novelanalysis.interfaces.rest.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
}
