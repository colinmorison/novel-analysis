package com.colin.novelanalysis.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 小说上传请求
 */
@Data
public class NovelUploadRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String author;
}
