package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.ApprovalService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.cenum.ApprovalStatus;
import com.zsx.cstfilemanage.domain.model.entity.ApprovalRecord;
import com.zsx.cstfilemanage.interfaces.http.request.ApprovalRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批控制器
 */
@RestController
@RequestMapping("/api/v1/approvals")
@Slf4j
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
        log.info("提交审批请求 - 文档ID: {}", documentId);
        try {
            approvalService.submitForApproval(documentId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("提交审批失败 - 文档ID: {}, 错误信息: {}", documentId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 审批文档
     */
    @PostMapping("/approve/{documentId}")
    public ApiResponse<Void> approveDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody ApprovalRequest request) {
        log.info("审批文档请求 - 文档ID: {}, 审批状态: {}, 审批意见: {}",
                documentId, request.getStatus(), request.getComment());
        try {
            approvalService.approveDocument(
                    documentId,
                    request.getStatus(),
                    request.getComment()
            );
            log.info("审批文档成功 - 文档ID: {}, 审批状态: {}", documentId, request.getStatus());
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("审批文档失败 - 文档ID: {}, 错误信息: {}", documentId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 查询审批进度
     */
    @GetMapping("/progress/{documentId}")
    public ApiResponse<List<ApprovalRecord>> getApprovalProgress(@PathVariable Long documentId) {
        log.info("查询审批进度请求 - 文档ID: {}", documentId);
        try {
            List<ApprovalRecord> records = approvalService.getApprovalProgress(documentId);
            log.info("查询审批进度成功 - 文档ID: {}, 审批记录数: {}", documentId, records.size());
            return ApiResponse.success(records);
        } catch (Exception e) {
            log.error("查询审批进度失败 - 文档ID: {}, 错误信息: {}", documentId, e.getMessage(), e);
            throw e;
        }
    }
}

