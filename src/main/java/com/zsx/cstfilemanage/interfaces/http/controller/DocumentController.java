package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.DocumentService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.interfaces.http.request.DocumentUploadRequest;
import com.zsx.cstfilemanage.interfaces.http.response.DocumentResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 文档管理控制器
 */
@RestController
@RequestMapping("/api/v1/documents")
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * 上传文档
     */
    @PostMapping("/upload")
    public ApiResponse<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileNumber") String fileNumber,
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "productModel", required = false) String productModel,
            @RequestParam("version") String version,
            @RequestParam("compileDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime compileDate,
            @RequestParam(value = "description", required = false) String description) {
        log.debug("=== 上传文档接口调用开始 ===");
        log.info("上传文档请求 - 文件编号: {}, 文件名称: {}, 版本: {}, 文件大小: {} bytes, 文件类型: {}", 
                fileNumber, fileName, version, file.getSize(), file.getContentType());
        try {
            Document document = documentService.uploadDocument(
                    file, fileNumber, fileName, productModel, version, compileDate, description);
            log.info("上传文档成功 - 文档ID: {}, 文件编号: {}, 文件名称: {}", 
                    document.getId(), document.getFileNumber(), document.getFileName());
            log.debug("=== 上传文档接口调用结束 ===");
            return ApiResponse.success(DocumentResponse.from(document));
        } catch (Exception e) {
            log.error("上传文档失败 - 文件编号: {}, 文件名称: {}, 错误信息: {}", 
                    fileNumber, fileName, e.getMessage(), e);
            return ApiResponse.error(500, "上传失败: " + e.getMessage());
        }
    }

    /**
     * 查询文档详情
     */
    @GetMapping("/{id}")
    public ApiResponse<DocumentResponse> getDocument(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id);
            log.info("查询文档详情成功 - 文档ID: {}, 文件编号: {}, 文件名称: {}", 
                    document.getId(), document.getFileNumber(), document.getFileName());
            return ApiResponse.success(DocumentResponse.from(document));
        } catch (Exception e) {
            log.error("查询文档详情失败 - 文档ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}

