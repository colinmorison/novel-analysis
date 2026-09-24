package com.colin.novelanalysis.domain.service;

/**
 * 解析任务生产者
 */
public interface ParseTaskProducer {

    void send(Long novelId);
}
