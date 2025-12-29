package com.zsx.cstfilemanage.interfaces.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 文档上传请求
 */
@Data
public class DocumentUploadRequest {

    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    @NotBlank(message = "文件编号不能为空")
    private String fileNumber;

    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    private String productModel;

    @NotBlank(message = "版本号不能为空")
    private String version;

    @NotNull(message = "编制日期不能为空")
    private LocalDateTime compileDate;

    private String description;
}

