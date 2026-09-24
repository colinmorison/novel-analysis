package com.colin.novelanalysis.interfaces.rest.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 小说上传响应
 */
@Data
@Builder
public class NovelUploadResponse {

    private Long novelId;

    private String title;

    private String parseStatus;
}
