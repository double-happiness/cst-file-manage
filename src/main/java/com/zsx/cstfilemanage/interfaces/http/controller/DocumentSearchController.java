package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.DocumentSearchService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.interfaces.http.request.DocumentSearchRequest;
import com.zsx.cstfilemanage.interfaces.http.response.DocumentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * 文档搜索控制器
 */
@RestController
@RequestMapping("/api/v1/documents/search")
@Slf4j
public class DocumentSearchController {

    private final DocumentSearchService documentSearchService;

    public DocumentSearchController(DocumentSearchService documentSearchService) {
        this.documentSearchService = documentSearchService;
    }

    /**
     * 搜索文档（POST）
     */
    @PostMapping
    public ApiResponse<Page<DocumentResponse>> searchDocuments(
            @RequestBody DocumentSearchRequest request) {

        log.info("搜索文档请求: {}", request);

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        Page<Document> documents = documentSearchService.searchDocuments(
                request.getFileNumber(),
                request.getFileName(),
                request.getProductModel(),
                request.getStatus(),
                request.getCompilerId(),
                pageable
        );

        Page<DocumentResponse> responses = documents.map(DocumentResponse::from);

        log.info("搜索文档成功 - 总数: {}", documents.getTotalElements());
        return ApiResponse.success(responses);
    }
}


