package com.zsx.cstfilemanage.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件下发记录
 */
@Entity
@Table(name = "document_distributions")
@Data
public class DocumentDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文档ID
     */
    @Column(nullable = false)
    private Long documentId;

    /**
     * 下发人ID
     */
    @Column(nullable = false)
    private Long distributorId;

    /**
     * 下发人姓名
     */
    @Column(nullable = false, length = 100)
    private String distributorName;

    /**
     * 下发说明
     */
    @Column(columnDefinition = "TEXT")
    private String distributionNote;

    /**
     * 生效日期
     */
    @Column
    private LocalDateTime effectiveDate;

    /**
     * 下发对象类型（DEPARTMENT部门、POSITION岗位、USER_GROUP用户组、USER用户）
     */
    @Column(nullable = false, length = 50)
    private String targetType;

    /**
     * 下发对象ID（JSON数组，根据targetType存储对应的ID列表）
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String targetIds;

    /**
     * 下发对象名称（JSON数组，存储对应的名称列表）
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String targetNames;

    /**
     * 下发时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime distributeTime;

    @PrePersist
    protected void onCreate() {
        distributeTime = LocalDateTime.now();
    }
}

