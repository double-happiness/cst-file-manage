package com.zsx.cstfilemanage.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.common.util.JsonUtil;
import com.zsx.cstfilemanage.domain.cenum.ApprovalStatus;
import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import com.zsx.cstfilemanage.domain.model.entity.ApprovalFlow;
import com.zsx.cstfilemanage.domain.model.entity.ApprovalRecord;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.domain.model.entity.User;
import com.zsx.cstfilemanage.domain.repository.ApprovalFlowRepository;
import com.zsx.cstfilemanage.domain.repository.ApprovalRecordRepository;
import com.zsx.cstfilemanage.domain.repository.DocumentRepository;
import com.zsx.cstfilemanage.domain.repository.UserRepository;
import com.zsx.cstfilemanage.domain.repository.UserRoleRepository;
import com.zsx.cstfilemanage.infrastructure.security.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 审批服务
 */
@Service
@Slf4j
public class ApprovalService {

    private final DocumentRepository documentRepository;
    private final ApprovalFlowRepository approvalFlowRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public ApprovalService(DocumentRepository documentRepository,
                          ApprovalFlowRepository approvalFlowRepository,
                          ApprovalRecordRepository approvalRecordRepository,
                          UserRepository userRepository,
                          UserRoleRepository userRoleRepository,
                          NotificationService notificationService) {
        this.documentRepository = documentRepository;
        this.approvalFlowRepository = approvalFlowRepository;
        this.approvalRecordRepository = approvalRecordRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.notificationService = notificationService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 提交审批
     */
    @Transactional
    public void submitForApproval(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BizException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (document.getStatus() != DocumentStatus.DRAFT) {
            throw new BizException(ErrorCode.DOCUMENT_STATUS_INVALID);
        }

        // 查找适用的审批流程
        ApprovalFlow approvalFlow = findApplicableApprovalFlow(document);
        if (approvalFlow == null) {
            throw new BizException(ErrorCode.APPROVAL_FLOW_NOT_FOUND);
        }

        // 解析审批环节
        List<Map<String, Object>> steps = parseApprovalSteps(approvalFlow.getApprovalSteps());

        // 创建审批记录
        for (int i = 0; i < steps.size(); i++) {
            Map<String, Object> step = steps.get(i);
            ApprovalRecord record = new ApprovalRecord();
            record.setDocumentId(documentId);
            record.setApprovalFlowId(approvalFlow.getId());
            record.setStepOrder(i + 1);
            record.setApproverRoleId(Long.valueOf(step.get("roleId").toString()));
            record.setApproverRoleName(step.get("roleName").toString());
            record.setStatus(ApprovalStatus.PENDING);
            
            // 第一个环节需要设置审批人（根据角色查找用户）
            if (i == 0) {
                Long approverId = findApproverByRole(record.getApproverRoleId());
                if (approverId == null) {
                    throw new BizException(ErrorCode.APPROVER_NOT_FOUND);
                }
                User approver = userRepository.findById(approverId)
                        .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));
                record.setApproverId(approverId);
                record.setApproverName(approver.getRealName());
                
                // 发送通知给第一个审批人
                notificationService.sendApprovalNotification(approver, document);
            }

            approvalRecordRepository.save(record);
        }

        // 更新文档状态
        document.setStatus(DocumentStatus.PENDING_APPROVAL);
        document.setCurrentApprovalFlowId(approvalFlow.getId());
        documentRepository.save(document);

        // TODO: 发送通知给第一个审批人
    }

    /**
     * 审批文档
     */
    @Transactional
    public void approveDocument(Long documentId, ApprovalStatus status, String comment) {
        Long userId = SecurityContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BizException(new ErrorCode(1006, "文档不存在")));

        // 查找当前审批记录
        ApprovalRecord record = approvalRecordRepository
                .findPendingByDocumentAndApprover(documentId, userId, ApprovalStatus.PENDING)
                .orElseThrow(() -> new BizException(ErrorCode.APPROVAL_RECORD_NOT_FOUND));

        // 更新审批记录
        record.setStatus(status);
        record.setComment(comment);
        record.setApproveTime(java.time.LocalDateTime.now());
        approvalRecordRepository.save(record);

        // 处理审批结果
        if (status == ApprovalStatus.APPROVED) {
            // 检查是否还有下一环节
            List<ApprovalRecord> nextSteps = approvalRecordRepository
                    .findCurrentStepRecords(documentId, ApprovalStatus.PENDING);
            
            if (nextSteps.isEmpty()) {
                // 所有环节都已完成，审批通过
                document.setStatus(DocumentStatus.APPROVED);
                // 通知上传人审批通过
                User uploader = userRepository.findById(document.getCreateUserId())
                        .orElse(null);
                if (uploader != null) {
                    notificationService.sendApprovalPassedNotification(uploader, document);
                }
            } else {
                // 流转到下一环节
                ApprovalRecord nextStep = nextSteps.get(0);
                // 根据角色查找审批人
                Long nextApproverId = findApproverByRole(nextStep.getApproverRoleId());
                if (nextApproverId != null) {
                    User nextApprover = userRepository.findById(nextApproverId).orElse(null);
                    if (nextApprover != null) {
                        nextStep.setApproverId(nextApproverId);
                        nextStep.setApproverName(nextApprover.getRealName());
                        approvalRecordRepository.save(nextStep);
                        // 发送通知给下一环节审批人
                        notificationService.sendApprovalNotification(nextApprover, document);
                    }
                }
                document.setStatus(DocumentStatus.APPROVING);
            }
        } else if (status == ApprovalStatus.REJECTED || status == ApprovalStatus.MODIFY_REQUIRED) {
            // 驳回，退回给上传人
            document.setStatus(DocumentStatus.REJECTED);
            // 通知上传人
            User uploader = userRepository.findById(document.getCreateUserId())
                    .orElse(null);
            if (uploader != null) {
                notificationService.sendApprovalRejectedNotification(uploader, document, comment);
            }
        }

        documentRepository.save(document);
    }

    /**
     * 查找适用的审批流程
     */
    private ApprovalFlow findApplicableApprovalFlow(Document document) {
        List<ApprovalFlow> flows = approvalFlowRepository.findByEnabledTrue();
        
        for (ApprovalFlow flow : flows) {
            // 简化匹配逻辑，实际应该更复杂
            if (isFlowApplicable(flow, document)) {
                return flow;
            }
        }
        
        return null;
    }

    /**
     * 判断流程是否适用
     */
    private boolean isFlowApplicable(ApprovalFlow flow, Document document) {
        // 简化实现，实际应该根据文件类型、产品型号、重要程度等匹配
        return true;
    }

    /**
     * 解析审批环节
     */
    private List<Map<String, Object>> parseApprovalSteps(String stepsJson) {
        try {
            return objectMapper.readValue(stepsJson, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new BizException(ErrorCode.APPROVAL_FLOW_CONFIG_ERROR);
        }
    }

    /**
     * 根据角色查找审批人
     */
    private Long findApproverByRole(Long roleId) {
        // 查询拥有该角色的用户
        List<Long> userIds = userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds.isEmpty()) {
            return null;
        }
        
        // 返回第一个启用的用户
        for (Long userId : userIds) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getEnabled()) {
                return userId;
            }
        }
        
        return null;
    }

    /**
     * 查询审批进度
     */
    public List<ApprovalRecord> getApprovalProgress(Long documentId) {
        return approvalRecordRepository.findByDocumentIdOrderByStepOrderAsc(documentId);
    }
}

