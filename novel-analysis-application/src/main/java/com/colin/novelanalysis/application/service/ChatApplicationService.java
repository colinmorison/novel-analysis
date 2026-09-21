package com.colin.novelanalysis.application.service;

import com.colin.novelanalysis.application.dto.ChatCommand;
import com.colin.novelanalysis.application.dto.ChatResult;
import com.colin.novelanalysis.domain.service.AiChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

/**
 * 聊天应用服务
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ChatApplicationService {

    private final AiChatService aiChatService;

    public ChatResult chat(@Valid ChatCommand command) {
        log.info("[chat] session={} message={}", command.getSessionId(),
                StringUtils.abbreviate(command.getUserMessage(), 50));
        String reply = aiChatService.chat(command.getSessionId(), command.getUserMessage());
        return ChatResult.builder()
                .sessionId(command.getSessionId())
                .reply(reply)
                .build();
    }
}
