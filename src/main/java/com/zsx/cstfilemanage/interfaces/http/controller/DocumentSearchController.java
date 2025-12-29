package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.DocumentSearchService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.interfaces.http.response.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * 文档搜索控制器
 */
@RestController
@RequestMapping("/api/v1/documents/search")
public class DocumentSearchController {

    private final DocumentSearchService documentSearchService;

    public DocumentSearchController(DocumentSearchService documentSearchService) {
        this.documentSearchService = documentSearchService;
    }

    /**
     * 搜索文档
     */
    @GetMapping
    public ApiResponse<Page<DocumentResponse>> searchDocuments(
            @RequestParam(required = false) String fileNumber,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String productModel,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) Long compilerId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Document> documents = documentSearchService.searchDocuments(
                fileNumber, fileName, productModel, status, compilerId, pageable
        );
        Page<DocumentResponse> responses = documents.map(DocumentResponse::from);
        return ApiResponse.success(responses);
    }
}

