package com.colin.novelanalysis.application.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 小说上传结果
 */
@Data
@Builder
public class NovelUploadResult {

    private Long novelId;

    private String title;

    private String parseStatus;
}
