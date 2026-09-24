package com.colin.novelanalysis.application.service;

import com.colin.novelanalysis.application.dto.NovelUploadCommand;
import com.colin.novelanalysis.application.dto.NovelUploadResult;
import com.colin.novelanalysis.domain.model.Novel;
import com.colin.novelanalysis.domain.model.ParseStatus;
import com.colin.novelanalysis.domain.repository.NovelRepository;
import com.colin.novelanalysis.domain.service.FileStorageService;
import com.colin.novelanalysis.domain.service.ParseTaskProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * 小说上传应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovelUploadAppService {

    private final NovelRepository novelRepository;
    private final FileStorageService fileStorageService;
    private final ParseTaskProducer parseProducer;

    @Transactional(rollbackFor = Exception.class)
    public NovelUploadResult upload(NovelUploadCommand command, InputStream inputStream) {
        Novel novel = Novel.builder()
                .title(command.getTitle())
                .author(command.getAuthor())
                .fileName(command.getFileName())
                .fileSize(command.getFileSize())
                .parseStatus(ParseStatus.PENDING.name())
                .uploadedAt(LocalDateTime.now())
                .build();
        novelRepository.save(novel);

        String path = "novels/" + novel.getId() + "/" + command.getFileName();
        try (InputStream is = inputStream) {
            fileStorageService.upload(path, is, command.getFileSize(), command.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }

        novel.setMinioPath(path);
        novelRepository.updateStatus(novel);

        parseProducer.send(novel.getId());

        return NovelUploadResult.builder()
                .novelId(novel.getId())
                .title(novel.getTitle())
                .parseStatus(novel.getParseStatus())
                .build();
    }
}
