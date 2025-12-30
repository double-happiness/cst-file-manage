package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.domain.repository.DocumentRepository;
import com.zsx.cstfilemanage.infrastructure.security.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;


import java.time.LocalDateTime;
import java.util.List;

/**
 * 版本管理服务
 */
@Service
@Slf4j
public class VersionService {

    private final DocumentRepository documentRepository;
    private final FileCompareService fileCompareService;

    public VersionService(DocumentRepository documentRepository, FileCompareService fileCompareService) {
        this.documentRepository = documentRepository;
        this.fileCompareService = fileCompareService;
    }

    /**
     * 创建新版本
     */
    @Transactional
    public Document createNewVersion(Long documentId,
                                     String newVersion,
                                     String changeDescription,
                                     String changeReason,
                                     LocalDateTime changeDate) {
        Long userId = SecurityContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        // 获取当前版本
        Document currentVersion = documentRepository.findById(documentId)
                .orElseThrow(() -> new BizException(ErrorCode.DOCUMENT_NOT_FOUND));

        // 检查版本号是否已存在
        if (documentRepository.findByFileNumberAndVersion(
                currentVersion.getFileNumber(), newVersion).isPresent()) {
            throw new BizException(ErrorCode.VERSION_EXISTS);
        }

        // 创建新版本文档（复制当前版本信息）
        Document newVersionDoc = new Document();
        newVersionDoc.setFileNumber(currentVersion.getFileNumber());
        newVersionDoc.setFileName(currentVersion.getFileName());
        newVersionDoc.setOriginalName(currentVersion.getOriginalName());
        newVersionDoc.setProductModel(currentVersion.getProductModel());
        newVersionDoc.setVersion(newVersion);
        newVersionDoc.setCompileDate(changeDate);
        newVersionDoc.setCompilerId(userId);
        newVersionDoc.setCompilerName(SecurityContext.getCurrentUserName());
        newVersionDoc.setDescription(changeDescription);
        newVersionDoc.setFileType(currentVersion.getFileType());
        newVersionDoc.setFileSize(currentVersion.getFileSize());
        newVersionDoc.setFilePath(currentVersion.getFilePath()); // 实际应该上传新文件
        newVersionDoc.setThumbnailPath(currentVersion.getThumbnailPath());
        newVersionDoc.setContentType(currentVersion.getContentType());
        newVersionDoc.setStatus(DocumentStatus.DRAFT);
        newVersionDoc.setIsCurrentVersion(false); // 新版本需要审批后才能成为当前版本
        newVersionDoc.setParentVersionId(currentVersion.getId());
        newVersionDoc.setCreateUserId(userId);
        newVersionDoc.setUpdateUserId(userId);

        return documentRepository.save(newVersionDoc);
    }

    /**
     * 查询文档的所有版本
     */
    public List<Document> getDocumentVersions(String fileNumber) {
        return documentRepository.findByFileNumberOrderByVersionDesc(fileNumber);
    }

    /**
     * 恢复历史版本为当前版本（需要审批）
     */
    @Transactional
    public void restoreVersion(Long versionId) {
        Long userId = SecurityContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        Document versionDoc = documentRepository.findById(versionId)
                .orElseThrow(() -> new BizException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (versionDoc.getIsCurrentVersion()) {
            throw new BizException(ErrorCode.VERSION_ALREADY_CURRENT);
        }

        // 将当前版本设为非当前版本
        Document currentVersion = documentRepository
                .findByFileNumberAndIsCurrentVersionTrue(versionDoc.getFileNumber())
                .orElse(null);

        if (currentVersion != null) {
            currentVersion.setIsCurrentVersion(false);
            documentRepository.save(currentVersion);
        }

        // 恢复指定版本为当前版本，但需要重新审批
        versionDoc.setIsCurrentVersion(true);
        versionDoc.setStatus(DocumentStatus.DRAFT);
        documentRepository.save(versionDoc);

        // TODO: 提交审批流程
    }

    /**
     * 版本对比
     */
    public VersionComparisonResult compareVersions(Long version1Id, Long version2Id) throws IOException {
        FileCompareService.FileCompareResult compareResult = fileCompareService.compareDocuments(version1Id, version2Id);

        VersionComparisonResult result = new VersionComparisonResult();
        result.setVersion1(compareResult.getVersion1());
        result.setVersion2(compareResult.getVersion2());
        result.setDifferences(compareResult.getDifferences());
        result.setHasDifferences(compareResult.isHasDifferences());

        return result;
    }

    /**
     * 版本对比结果
     */
    public static class VersionComparisonResult {
        private Document version1;
        private Document version2;
        private String differences;
        private boolean hasDifferences;

        // Getters and Setters
        public Document getVersion1() {
            return version1;
        }

        public void setVersion1(Document version1) {
            this.version1 = version1;
        }

        public Document getVersion2() {
            return version2;
        }

        public void setVersion2(Document version2) {
            this.version2 = version2;
        }

        public String getDifferences() {
            return differences;
        }

        public void setDifferences(String differences) {
            this.differences = differences;
        }

        public boolean isHasDifferences() {
            return hasDifferences;
        }

        public void setHasDifferences(boolean hasDifferences) {
            this.hasDifferences = hasDifferences;
        }
    }
}

