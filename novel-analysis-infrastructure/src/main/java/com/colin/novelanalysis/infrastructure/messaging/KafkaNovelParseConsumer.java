package com.colin.novelanalysis.infrastructure.messaging;

import com.colin.novelanalysis.domain.service.NovelParseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 小说解析任务消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNovelParseConsumer {

    private final NovelParseService novelParseService;

    @KafkaListener(topics = KafkaNovelParseProducer.TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        log.info("[Kafka] receive parse task, novelId={}", message);
        try {
            Long novelId = Long.valueOf(message);
            novelParseService.parse(novelId);
        } catch (Exception e) {
            log.error("[Kafka] parse failed, novelId={}", message, e);
        }
    }
}
