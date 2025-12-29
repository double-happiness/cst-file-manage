package com.zsx.cstfilemanage.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    UNAUTHORIZED(401, "未登录或登录已过期"),
    FILE_TYPE_NOT_ALLOWED(1001, "文件类型不允许"),
    FILE_TOO_LARGE(1002, "文件过大"),
    BIZ_TYPE_NOT_SUPPORTED(1003, "业务类型不支持"),
    UPLOAD_TOO_FREQUENT(1004, "上传太频繁"),
    FILE_SIZE_INVALID(1004, "上传太频繁"),
    INTERNAL_ERROR(500, "系统内部错误");

    private final int code;
    private final String message;
}
