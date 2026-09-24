package com.colin.novelanalysis.interfaces.rest.controller;

import com.colin.novelanalysis.application.dto.NovelUploadCommand;
import com.colin.novelanalysis.application.dto.NovelUploadResult;
import com.colin.novelanalysis.application.service.NovelUploadAppService;
import com.colin.novelanalysis.interfaces.rest.dto.ApiResult;
import com.colin.novelanalysis.interfaces.rest.dto.NovelUploadRequest;
import com.colin.novelanalysis.interfaces.rest.dto.NovelUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 小说上传接口
 */
@RestController
@RequestMapping("/api/v1/novels")
@RequiredArgsConstructor
@Validated
public class NovelUploadController {

    private final NovelUploadAppService novelUploadAppService;

    @PostMapping("/upload")
    public ApiResult<NovelUploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("request") @Validated NovelUploadRequest request) {
        try {
            NovelUploadResult result = novelUploadAppService.upload(NovelUploadCommand.builder()
                    .title(request.getTitle())
                    .author(request.getAuthor())
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .build(), file.getInputStream());
            return ApiResult.ok(NovelUploadResponse.builder()
                    .novelId(result.getNovelId())
                    .title(result.getTitle())
                    .parseStatus(result.getParseStatus())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("上传失败", e);
        }
    }
}
