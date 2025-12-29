package com.zsx.cstfilemanage.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门实体
 */
@Entity
@Table(name = "departments")
@Data
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 部门代码
     */
    @Column(nullable = false, unique = true, length = 50)
    private String departmentCode;

    /**
     * 部门名称
     */
    @Column(nullable = false, length = 100)
    private String departmentName;

    /**
     * 父部门ID
     */
    @Column
    private Long parentId;

    /**
     * 部门描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

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

