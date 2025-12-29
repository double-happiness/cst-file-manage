package com.zsx.cstfilemanage.interfaces.http.response;

import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import com.zsx.cstfilemanage.domain.cenum.FileType;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档响应
 */
@Data
public class DocumentResponse {

    private Long id;
    private String fileNumber;
    private String fileName;
    private String originalName;
    private String productModel;
    private String version;
    private LocalDateTime compileDate;
    private Long compilerId;
    private String compilerName;
    private String description;
    private FileType fileType;
    private Long fileSize;
    private String filePath;
    private String thumbnailPath;
    private String contentType;
    private DocumentStatus status;
    private Boolean isCurrentVersion;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static DocumentResponse from(Document document) {
        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setFileNumber(document.getFileNumber());
        response.setFileName(document.getFileName());
        response.setOriginalName(document.getOriginalName());
        response.setProductModel(document.getProductModel());
        response.setVersion(document.getVersion());
        response.setCompileDate(document.getCompileDate());
        response.setCompilerId(document.getCompilerId());
        response.setCompilerName(document.getCompilerName());
        response.setDescription(document.getDescription());
        response.setFileType(document.getFileType());
        response.setFileSize(document.getFileSize());
        response.setFilePath(document.getFilePath());
        response.setThumbnailPath(document.getThumbnailPath());
        response.setContentType(document.getContentType());
        response.setStatus(document.getStatus());
        response.setIsCurrentVersion(document.getIsCurrentVersion());
        response.setCreateTime(document.getCreateTime());
        response.setUpdateTime(document.getUpdateTime());
        return response;
    }
}

