package com.colin.novelanalysis.application.service;

import com.colin.novelanalysis.application.dto.ChatCommand;
import com.colin.novelanalysis.application.dto.ChatResult;
import com.colin.novelanalysis.domain.model.ChatMessage;
import com.colin.novelanalysis.domain.model.KnowledgeChunk;
import com.colin.novelanalysis.domain.model.Novel;
import com.colin.novelanalysis.domain.repository.KnowledgeRepository;
import com.colin.novelanalysis.domain.repository.NovelRepository;
import com.colin.novelanalysis.domain.service.AiChatService;
import com.colin.novelanalysis.domain.service.ChatSessionRepository;
import com.colin.novelanalysis.domain.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * 聊天应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAppService {

    private final AiChatService aiChatService;
    private final EmbeddingService embeddingService;
    private final KnowledgeRepository knowledgeRepository;
    private final NovelRepository novelRepository;
    private final ChatSessionRepository chatSessionRepository;

    public ChatResult chat(ChatCommand command) {
        String question = command.getUserMessage();
        log.info("[chat] session={} question={}", command.getSessionId(), StringUtils.abbreviate(question, 50));

        String prompt = buildPrompt(command.getSessionId(), question);
        String reply = aiChatService.chat(command.getSessionId(), prompt);

        chatSessionRepository.addMessage(command.getSessionId(), ChatMessage.builder()
                .role("user").content(question).timestamp(LocalDateTime.now()).build());
        chatSessionRepository.addMessage(command.getSessionId(), ChatMessage.builder()
                .role("assistant").content(reply).timestamp(LocalDateTime.now()).build());

        return ChatResult.builder()
                .sessionId(command.getSessionId())
                .reply(reply)
                .build();
    }

    public void streamChat(ChatCommand command, Consumer<String> onChunk, Runnable onComplete) {
        String question = command.getUserMessage();
        log.info("[streamChat] session={} question={}", command.getSessionId(), StringUtils.abbreviate(question, 50));

        String prompt = buildPrompt(command.getSessionId(), question);
        StringBuilder reply = new StringBuilder();

        aiChatService.streamChat(command.getSessionId(), prompt, token -> {
            reply.append(token);
            onChunk.accept(token);
        });

        chatSessionRepository.addMessage(command.getSessionId(), ChatMessage.builder()
                .role("user").content(question).timestamp(LocalDateTime.now()).build());
        chatSessionRepository.addMessage(command.getSessionId(), ChatMessage.builder()
                .role("assistant").content(reply.toString()).timestamp(LocalDateTime.now()).build());

        onComplete.run();
    }

    public Flux<String> streamChat(ChatCommand command) {
        String question = command.getUserMessage();
        log.info("[streamChatFlux] session={} question={}", command.getSessionId(), StringUtils.abbreviate(question, 50));

        String prompt = buildPrompt(command.getSessionId(), question);
        StringBuilder reply = new StringBuilder();

        return aiChatService.streamChatFlux(command.getSessionId(), prompt)
                .doOnNext(token -> reply.append(token))
                .doOnComplete(() -> {
                    chatSessionRepository.addMessage(command.getSessionId(), ChatMessage.builder()
                            .role("user").content(question).timestamp(LocalDateTime.now()).build());
                    chatSessionRepository.addMessage(command.getSessionId(), ChatMessage.builder()
                            .role("assistant").content(reply.toString()).timestamp(LocalDateTime.now()).build());
                });
    }

    private String buildPrompt(String sessionId, String question) {
        float[] embedding = embeddingService.embed(question);

        // 全局库：取所有小说中召回最相关的 Top-5
        StringBuilder context = new StringBuilder();
        List<Novel> novels = novelRepository.findAll();
        int maxChunksPerNovel = 3;
        for (Novel novel : novels) {
            List<KnowledgeChunk> chunks = knowledgeRepository.searchSimilar(novel.getId(), embedding, maxChunksPerNovel);
            if (chunks.isEmpty()) {
                continue;
            }
            context.append("\n【小说：").append(novel.getTitle()).append("】\n");
            for (KnowledgeChunk chunk : chunks) {
                context.append("[").append(chunk.getChapterTitle()).append("] ")
                        .append(chunk.getContent()).append("\n");
            }
        }

        String history = buildHistory(sessionId);

        return "你是一位小说分析助手，请根据以下小说原文片段回答问题。若片段中没有足够信息，请明确说明。\n\n" +
                "【历史对话】\n" + history + "\n" +
                "【相关原文片段】\n" + context + "\n" +
                "【用户问题】\n" + question;
    }

    private String buildHistory(String sessionId) {
        List<ChatMessage> messages = chatSessionRepository.getHistory(sessionId);
        if (messages.isEmpty()) {
            return "（无）";
        }
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : messages) {
            sb.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
        }
        return sb.toString();
    }
}
