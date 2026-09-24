package com.colin.novelanalysis.interfaces.rest.controller;

import com.colin.novelanalysis.application.dto.NovelUploadCommand;
import com.colin.novelanalysis.application.dto.NovelUploadResult;
import com.colin.novelanalysis.application.service.NovelUploadAppService;
import com.colin.novelanalysis.interfaces.rest.dto.ApiResult;
import com.colin.novelanalysis.interfaces.rest.dto.NovelUploadRequest;
import com.colin.novelanalysis.interfaces.rest.dto.NovelUploadResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * 小说上传接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/novels")
@RequiredArgsConstructor
@Validated
public class NovelUploadController {

    private final NovelUploadAppService novelUploadAppService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ApiResult<NovelUploadResponse>> upload(
            @RequestPart("file") Mono<FilePart> filePartMono,
            @RequestPart("request") Mono<FormFieldPart> requestPartMono) {
        return Mono.zip(filePartMono, requestPartMono)
                .flatMap(tuple -> {
                    FilePart filePart = tuple.getT1();
                    FormFieldPart requestPart = tuple.getT2();
                    NovelUploadRequest request = parseRequest(requestPart);
                    return doUpload(filePart, request);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<ApiResult<NovelUploadResponse>> doUpload(FilePart filePart, NovelUploadRequest request) {
        Path tempFile;
        try {
            tempFile = Files.createTempFile("novel-", "-" + filePart.filename());
        } catch (Exception e) {
            return Mono.error(new RuntimeException("创建临时文件失败", e));
        }
        return filePart.transferTo(tempFile)
                .then(Mono.fromCallable(() -> {
                    try (InputStream is = Files.newInputStream(tempFile)) {
                        NovelUploadResult result = novelUploadAppService.upload(NovelUploadCommand.builder()
                                .title(request.getTitle())
                                .author(request.getAuthor())
                                .fileName(filePart.filename())
                                .fileSize(Files.size(tempFile))
                                .contentType("application/octet-stream")
                                .build(), is);
                        return ApiResult.ok(NovelUploadResponse.builder()
                                .novelId(result.getNovelId())
                                .title(result.getTitle())
                                .parseStatus(result.getParseStatus())
                                .build());
                    } finally {
                        Files.deleteIfExists(tempFile);
                    }
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    @SneakyThrows
    private NovelUploadRequest parseRequest(FormFieldPart part) {
        return objectMapper.readValue(part.value(), NovelUploadRequest.class);
    }
}
