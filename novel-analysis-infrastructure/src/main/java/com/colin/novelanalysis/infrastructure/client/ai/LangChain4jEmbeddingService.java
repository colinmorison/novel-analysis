package com.colin.novelanalysis.infrastructure.client.ai;

import com.colin.novelanalysis.domain.service.EmbeddingService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于 LangChain4j 的向量化服务实现
 */
@Component
@RequiredArgsConstructor
public class LangChain4jEmbeddingService implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

    @Override
    public float[] embed(String text) {
        Embedding embedding = embeddingModel.embed(text).content();
        return embedding.vector();
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        List<TextSegment> segments = texts.stream().map(TextSegment::from).toList();
        return embeddingModel.embedAll(segments).content().stream()
                .map(Embedding::vector)
                .toList();
    }
}
