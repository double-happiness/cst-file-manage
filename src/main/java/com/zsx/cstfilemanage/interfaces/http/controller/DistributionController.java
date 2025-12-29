package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.DistributionService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.interfaces.http.request.DistributionRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件下发控制器
 */
@RestController
@RequestMapping("/api/v1/distributions")
public class DistributionController {

    private final DistributionService distributionService;

    public DistributionController(DistributionService distributionService) {
        this.distributionService = distributionService;
    }

    /**
     * 下发文件
     */
    @PostMapping("/distribute")
    public ApiResponse<Void> distributeDocuments(@Valid @RequestBody DistributionRequest request) {
        distributionService.distributeDocuments(
                request.getDocumentIds(),
                request.getTargetType(),
                request.getTargetIds(),
                request.getTargetNames(),
                request.getDistributionNote(),
                request.getEffectiveDate()
        );
        return ApiResponse.success(null);
    }

    /**
     * 回收文件
     */
    @PostMapping("/recall")
    public ApiResponse<Void> recallDocuments(@RequestBody List<Long> documentIds) {
        distributionService.recallDocuments(documentIds);
        return ApiResponse.success(null);
    }

    /**
     * 作废文件
     */
    @PostMapping("/obsolete")
    public ApiResponse<Void> obsoleteDocuments(@RequestBody List<Long> documentIds) {
        distributionService.obsoleteDocuments(documentIds);
        return ApiResponse.success(null);
    }

    /**
     * 记录文件查看
     */
    @PostMapping("/view/{distributionId}")
    public ApiResponse<Void> recordView(@PathVariable Long distributionId) {
        distributionService.recordView(distributionId);
        return ApiResponse.success(null);
    }

    /**
     * 记录文件下载
     */
    @PostMapping("/download/{distributionId}")
    public ApiResponse<Void> recordDownload(@PathVariable Long distributionId) {
        distributionService.recordDownload(distributionId);
        return ApiResponse.success(null);
    }
}

