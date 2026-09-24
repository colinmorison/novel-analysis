package com.colin.novelanalysis.domain.service;

import java.io.InputStream;

/**
 * 文件存储服务
 */
public interface FileStorageService {

    String upload(String path, InputStream inputStream, long size, String contentType);

    InputStream download(String path);

    void delete(String path);
}
