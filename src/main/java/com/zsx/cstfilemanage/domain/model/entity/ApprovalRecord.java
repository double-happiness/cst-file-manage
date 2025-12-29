package com.zsx.cstfilemanage.domain.model.entity;

import com.zsx.cstfilemanage.domain.cenum.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批记录
 */
@Entity
@Table(name = "approval_records")
@Data
public class ApprovalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文档ID
     */
    @Column(nullable = false)
    private Long documentId;

    /**
     * 审批流程ID
     */
    @Column(nullable = false)
    private Long approvalFlowId;

    /**
     * 审批环节顺序（从1开始）
     */
    @Column(nullable = false)
    private Integer stepOrder;

    /**
     * 审批人ID
     */
    @Column(nullable = false)
    private Long approverId;

    /**
     * 审批人姓名
     */
    @Column(nullable = false, length = 100)
    private String approverName;

    /**
     * 审批人角色ID
     */
    @Column(nullable = false)
    private Long approverRoleId;

    /**
     * 审批人角色名称
     */
    @Column(nullable = false, length = 100)
    private String approverRoleName;

    /**
     * 审批状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApprovalStatus status;

    /**
     * 审批意见
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /**
     * 审批时间
     */
    @Column
    private LocalDateTime approveTime;

    /**
     * 创建时间（提交审批时间）
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (status == null) {
            status = ApprovalStatus.PENDING;
        }
    }
}

