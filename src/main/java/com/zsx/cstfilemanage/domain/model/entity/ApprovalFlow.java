package com.zsx.cstfilemanage.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批流程配置
 */
@Entity
@Table(name = "approval_flows")
@Data
public class ApprovalFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 流程名称
     */
    @Column(nullable = false, length = 100)
    private String flowName;

    /**
     * 适用文件类型（JSON数组，如：["CAD_DWG", "CAD_DXF"]）
     */
    @Column(columnDefinition = "TEXT")
    private String applicableFileTypes;

    /**
     * 适用产品型号（JSON数组，为空表示所有产品）
     */
    @Column(columnDefinition = "TEXT")
    private String applicableProductModels;

    /**
     * 重要程度（HIGH/MEDIUM/LOW）
     */
    @Column(length = 20)
    private String importanceLevel;

    /**
     * 审批环节配置（JSON数组，每个环节包含：order顺序、roleId角色ID、roleName角色名称）
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String approvalSteps;

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @Column(nullable = false)
    private Long createUserId;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}

