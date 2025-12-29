package com.zsx.cstfilemanage.domain.model.entity;

import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import com.zsx.cstfilemanage.domain.cenum.FileType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档实体
 */
@Entity
@Table(name = "documents")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文件唯一编号
     */
    @Column(nullable = false, unique = true, length = 100)
    private String fileNumber;

    /**
     * 文件名称
     */
    @Column(nullable = false, length = 255)
    private String fileName;

    /**
     * 原始文件名
     */
    @Column(nullable = false, length = 255)
    private String originalName;

    /**
     * 所属产品型号
     */
    @Column(length = 100)
    private String productModel;

    /**
     * 版本号
     */
    @Column(nullable = false, length = 50)
    private String version;

    /**
     * 编制日期
     */
    @Column(nullable = false)
    private LocalDateTime compileDate;

    /**
     * 编制人ID
     */
    @Column(nullable = false)
    private Long compilerId;

    /**
     * 编制人姓名
     */
    @Column(nullable = false, length = 100)
    private String compilerName;

    /**
     * 文件描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 文件类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FileType fileType;

    /**
     * 文件大小（字节）
     */
    @Column(nullable = false)
    private Long fileSize;

    /**
     * 文件存储路径/对象存储key
     */
    @Column(nullable = false, length = 500)
    private String filePath;

    /**
     * 缩略图路径
     */
    @Column(length = 500)
    private String thumbnailPath;

    /**
     * 文件MIME类型
     */
    @Column(length = 100)
    private String contentType;

    /**
     * 文档状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DocumentStatus status;

    /**
     * 当前审批流程ID
     */
    @Column
    private Long currentApprovalFlowId;

    /**
     * 是否当前有效版本
     */
    @Column(nullable = false)
    private Boolean isCurrentVersion = true;

    /**
     * 父版本ID（用于版本追溯）
     */
    @Column
    private Long parentVersionId;

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

    /**
     * 更新人ID
     */
    @Column(nullable = false)
    private Long updateUserId;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) {
            status = DocumentStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}

