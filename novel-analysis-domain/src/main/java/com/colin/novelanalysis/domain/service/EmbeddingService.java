package com.colin.novelanalysis.domain.service;

import java.util.List;

/**
 * 文本向量化服务
 */
public interface EmbeddingService {

    float[] embed(String text);

    List<float[]> embed(List<String> texts);
}
