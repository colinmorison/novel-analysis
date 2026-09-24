package com.colin.novelanalysis.infrastructure.messaging;

import com.colin.novelanalysis.domain.service.ParseTaskProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 小说解析任务生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNovelParseProducer implements ParseTaskProducer {

    public static final String TOPIC = "novel.parse";

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void send(Long novelId) {
        log.info("[Kafka] send parse task, novelId={}", novelId);
        kafkaTemplate.send(TOPIC, String.valueOf(novelId));
    }
}
