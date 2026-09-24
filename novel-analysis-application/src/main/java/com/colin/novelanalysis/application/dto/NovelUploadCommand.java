package com.colin.novelanalysis.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * 小说上传命令
 */
@Data
@Builder
public class NovelUploadCommand {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String author;

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    private long fileSize;

    private String contentType;
}
