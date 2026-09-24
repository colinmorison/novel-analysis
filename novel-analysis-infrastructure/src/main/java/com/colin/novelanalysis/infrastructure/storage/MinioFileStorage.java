package com.colin.novelanalysis.infrastructure.storage;

import com.colin.novelanalysis.domain.service.FileStorageService;
import com.colin.novelanalysis.infrastructure.config.MinioConfig;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * MinIO 文件存储实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioFileStorage implements FileStorageService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    @SneakyThrows
    public String upload(String path, InputStream inputStream, long size, String contentType) {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(path)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build());
        return path;
    }

    @Override
    @SneakyThrows
    public InputStream download(String path) {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(path)
                .build());
    }

    @Override
    @SneakyThrows
    public void delete(String path) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(path)
                .build());
    }
}
