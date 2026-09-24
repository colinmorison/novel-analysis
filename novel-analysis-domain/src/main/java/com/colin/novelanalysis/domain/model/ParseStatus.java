package com.colin.novelanalysis.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 小说解析状态
 */
@Getter
@RequiredArgsConstructor
public enum ParseStatus {

    PENDING("待解析"),
    PARSING("解析中"),
    CHUNKING("分块中"),
    EXTRACTING("抽取角色事件中"),
    COMPLETED("已完成"),
    FAILED("失败");

    private final String desc;
}
