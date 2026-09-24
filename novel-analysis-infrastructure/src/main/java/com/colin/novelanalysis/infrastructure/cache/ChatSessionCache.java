package com.colin.novelanalysis.infrastructure.cache;

import com.colin.novelanalysis.domain.model.ChatMessage;
import com.colin.novelanalysis.domain.service.ChatSessionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 聊天会话缓存
 */
@Component
@RequiredArgsConstructor
public class ChatSessionCache implements ChatSessionRepository {

    private static final String KEY_PREFIX = "chat:session:";
    private static final long EXPIRE_DAYS = 7;
    private static final int MAX_HISTORY = 20;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void addMessage(String sessionId, ChatMessage message) {
        String key = KEY_PREFIX + sessionId;
        List<ChatMessage> history = getHistory(sessionId);
        history.add(message);
        if (history.size() > MAX_HISTORY) {
            history = history.subList(history.size() - MAX_HISTORY, history.size());
        }
        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(history), EXPIRE_DAYS, TimeUnit.DAYS);
    }

    @SneakyThrows
    public List<ChatMessage> getHistory(String sessionId) {
        String key = KEY_PREFIX + sessionId;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(value, new TypeReference<>() {
        });
    }

    public void clear(String sessionId) {
        redisTemplate.delete(KEY_PREFIX + sessionId);
    }
}
