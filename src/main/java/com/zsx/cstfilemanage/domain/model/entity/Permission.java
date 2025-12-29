package com.zsx.cstfilemanage.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限实体
 */
@Entity
@Table(name = "permissions")
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 权限代码
     */
    @Column(nullable = false, unique = true, length = 100)
    private String permissionCode;

    /**
     * 权限名称
     */
    @Column(nullable = false, length = 100)
    private String permissionName;

    /**
     * 权限描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 权限类型（MODULE功能模块、OPERATION操作）
     */
    @Column(nullable = false, length = 20)
    private String permissionType;

    /**
     * 父权限ID（用于权限树结构）
     */
    @Column
    private Long parentId;

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

