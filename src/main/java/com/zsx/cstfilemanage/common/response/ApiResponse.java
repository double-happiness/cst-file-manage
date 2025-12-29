package com.zsx.cstfilemanage.common.response;

import com.zsx.cstfilemanage.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private int code;      // 业务状态码
    private String message;// 消息
    private T data;        // 数据

    // 成功返回
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }

    // 成功返回，没有数据
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(0, "success", null);
    }

    // 错误返回，自定义 code/message
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // 错误返回，使用 ErrorCode
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
}
