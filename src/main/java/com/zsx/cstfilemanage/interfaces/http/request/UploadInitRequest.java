package com.zsx.cstfilemanage.interfaces.http.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class UploadInitRequest {

    @NotBlank
    private String fileName;

    @NotNull
    private Long fileSize;

    @NotBlank
    private String contentType;

    @NotBlank
    private String bizType;

    // 可选：用于秒传 / 去重
    private String checksum;
}
