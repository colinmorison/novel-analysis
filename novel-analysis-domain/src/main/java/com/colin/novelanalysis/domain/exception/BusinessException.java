package com.colin.novelanalysis.domain.exception;

import lombok.Getter;

/**
 * 业务异常，带错误码
 */
@Getter
public class BusinessException extends DomainException {

    private final String errorCode;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
