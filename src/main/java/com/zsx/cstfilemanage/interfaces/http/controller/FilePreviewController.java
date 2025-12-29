package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.FilePreviewService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 文件预览控制器
 */
@RestController
@RequestMapping("/api/v1/preview")
public class FilePreviewController {

    private final FilePreviewService previewService;

    public FilePreviewController(FilePreviewService previewService) {
        this.previewService = previewService;
    }

    /**
     * 预览文件
     */
    @GetMapping("/{documentId}")
    public ResponseEntity<Resource> previewFile(@PathVariable Long documentId) throws IOException {
        Resource resource = previewService.getPreviewResource(documentId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    /**
     * PDF预览（返回第一页图片）
     */
    @GetMapping("/pdf/{documentId}")
    public ResponseEntity<byte[]> previewPdf(@PathVariable Long documentId) throws IOException {
        byte[] imageBytes = previewService.getPdfPreview(documentId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }

    /**
     * 图片预览
     */
    @GetMapping("/image/{documentId}")
    public ResponseEntity<Resource> previewImage(@PathVariable Long documentId) throws IOException {
        Resource resource = previewService.getImagePreview(documentId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}

