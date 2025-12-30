package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.VersionService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.interfaces.http.request.CreateVersionRequest;
import com.zsx.cstfilemanage.interfaces.http.response.DocumentResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 版本管理控制器
 */
@RestController
@RequestMapping("/api/v1/versions")
public class VersionController {

    private final VersionService versionService;

    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    /**
     * 创建新版本
     */
    @PostMapping("/create/{documentId}")
    public ApiResponse<DocumentResponse> createNewVersion(
            @PathVariable Long documentId,
            @Valid @RequestBody CreateVersionRequest request) {
        Document document = versionService.createNewVersion(
                documentId,
                request.getNewVersion(),
                request.getChangeDescription(),
                request.getChangeReason(),
                request.getChangeDate()
        );
        return ApiResponse.success(DocumentResponse.from(document));
    }

    /**
     * 查询文档的所有版本
     */
    @GetMapping("/{fileNumber}")
    public ApiResponse<List<DocumentResponse>> getDocumentVersions(@PathVariable String fileNumber) {
        List<Document> versions = versionService.getDocumentVersions(fileNumber);
        List<DocumentResponse> responses = versions.stream()
                .map(DocumentResponse::from)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    /**
     * 恢复历史版本
     */
    @PostMapping("/restore/{versionId}")
    public ApiResponse<Void> restoreVersion(@PathVariable Long versionId) {
        versionService.restoreVersion(versionId);
        return ApiResponse.success(null);
    }

    /**
     * 版本对比
     */
    @GetMapping("/compare")
    public ApiResponse<VersionService.VersionComparisonResult> compareVersions(
            @RequestParam Long version1Id,
            @RequestParam Long version2Id) throws IOException {
        VersionService.VersionComparisonResult result = versionService.compareVersions(version1Id, version2Id);
        return ApiResponse.success(result);
    }
}

