package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.DocumentService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.interfaces.http.request.DocumentUploadRequest;
import com.zsx.cstfilemanage.interfaces.http.response.DocumentResponse;
import jakarta.validation.Valid;
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
            @RequestParam("compileDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime compileDate,
            @RequestParam(value = "description", required = false) String description) {
        try {
            Document document = documentService.uploadDocument(
                    file, fileNumber, fileName, productModel, version, compileDate, description);
            return ApiResponse.success(DocumentResponse.from(document));
        } catch (Exception e) {
            return ApiResponse.error(500, "上传失败: " + e.getMessage());
        }
    }

    /**
     * 查询文档详情
     */
    @GetMapping("/{id}")
    public ApiResponse<DocumentResponse> getDocument(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        return ApiResponse.success(DocumentResponse.from(document));
    }
}

