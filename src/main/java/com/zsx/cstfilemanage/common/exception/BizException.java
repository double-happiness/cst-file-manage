package com.zsx.cstfilemanage.common.exception;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final int code;       // 业务错误码
    private final String message; // 错误信息

    // 使用 ErrorCode 枚举
    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    // 自定义 message
    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
    }

    // 直接使用 code + message
    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
