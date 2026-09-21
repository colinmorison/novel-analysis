package com.colin.novelanalysis.interfaces.rest.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 统一 API 响应结构
 */
@Data
@Builder
public class ApiResult<T> {

    private String code;
    private String message;
    private T data;

    public static <T> ApiResult<T> ok(T data) {
        return ApiResult.<T>builder().code("0").message("success").data(data).build();
    }

    public static <T> ApiResult<T> fail(String code, String message) {
        return ApiResult.<T>builder().code(code).message(message).build();
    }
}
