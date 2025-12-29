package com.zsx.cstfilemanage.domain.model.entity;

import com.zsx.cstfilemanage.domain.cenum.OperationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志
 */
@Entity
@Table(name = "operation_logs", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_operation_type", columnList = "operationType"),
    @Index(name = "idx_create_time", columnList = "createTime")
})
@Data
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 操作人ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 操作人姓名
     */
    @Column(nullable = false, length = 100)
    private String userName;

    /**
     * 操作类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OperationType operationType;

    /**
     * 操作内容
     */
    @Column(columnDefinition = "TEXT")
    private String operationContent;

    /**
     * 操作对象类型（DOCUMENT、USER、ROLE等）
     */
    @Column(length = 50)
    private String objectType;

    /**
     * 操作对象ID
     */
    @Column
    private Long objectId;

    /**
     * 操作结果（SUCCESS、FAILURE）
     */
    @Column(length = 20)
    private String result;

    /**
     * 错误信息（如果操作失败）
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 登录IP（用于登录日志）
     */
    @Column(length = 50)
    private String ipAddress;

    /**
     * 用户代理（User-Agent）
     */
    @Column(length = 500)
    private String userAgent;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}

