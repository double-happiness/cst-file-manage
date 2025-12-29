package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.ApprovalService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.cenum.ApprovalStatus;
import com.zsx.cstfilemanage.domain.model.entity.ApprovalRecord;
import com.zsx.cstfilemanage.interfaces.http.request.ApprovalRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批控制器
 */
@RestController
@RequestMapping("/api/v1/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    /**
     * 提交审批
     */
    @PostMapping("/submit/{documentId}")
    public ApiResponse<Void> submitForApproval(@PathVariable Long documentId) {
        approvalService.submitForApproval(documentId);
        return ApiResponse.success(null);
    }

    /**
     * 审批文档
     */
    @PostMapping("/approve/{documentId}")
    public ApiResponse<Void> approveDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody ApprovalRequest request) {
        approvalService.approveDocument(
                documentId,
                request.getStatus(),
                request.getComment()
        );
        return ApiResponse.success(null);
    }

    /**
     * 查询审批进度
     */
    @GetMapping("/progress/{documentId}")
    public ApiResponse<List<ApprovalRecord>> getApprovalProgress(@PathVariable Long documentId) {
        List<ApprovalRecord> records = approvalService.getApprovalProgress(documentId);
        return ApiResponse.success(records);
    }
}

