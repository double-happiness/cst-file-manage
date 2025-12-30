package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import com.zsx.cstfilemanage.domain.cenum.FileType;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.domain.repository.DocumentRepository;
import com.zsx.cstfilemanage.domain.storage.OcsClient;
import com.zsx.cstfilemanage.infrastructure.security.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文档服务
 */
@Service
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OcsClient ocsClient;
    private final FileTypeValidator fileTypeValidator;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public DocumentService(DocumentRepository documentRepository,
                          OcsClient ocsClient,
                          FileTypeValidator fileTypeValidator) {
        this.documentRepository = documentRepository;
        this.ocsClient = ocsClient;
        this.fileTypeValidator = fileTypeValidator;
    }

    /**
     * 上传文档
     */
    @Transactional
    public Document uploadDocument(MultipartFile file,
                                   String fileNumber,
                                   String fileName,
                                   String productModel,
                                   String version,
                                   LocalDateTime compileDate,
                                   String description) throws IOException {
        log.debug("=== DocumentService.uploadDocument 开始 ===");
        log.info("上传文档 - 文件编号: {}, 文件名称: {}, 版本: {}, 文件大小: {} bytes", 
                fileNumber, fileName, version, file.getSize());
        
        // 获取当前用户
        Long userId = SecurityContext.getCurrentUserId();
        if (userId == null) {
            log.error("上传文档失败 - 用户未授权");
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        log.debug("上传文档 - 当前用户ID: {}, 用户名: {}", userId, SecurityContext.getCurrentUserName());

        // 验证文件格式
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        FileType fileType = FileType.fromExtension(extension);
        log.info("上传文档 - 原始文件名: {}, 扩展名: {}, 文件类型: {}", originalFilename, extension, fileType);
        
        if (!fileTypeValidator.isAllowed(fileType, extension)) {
            log.warn("上传文档失败 - 文件类型不允许: {}, 扩展名: {}", fileType, extension);
            throw new BizException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }

        // 检查文件编号是否已存在
        if (documentRepository.findByFileNumberAndIsCurrentVersionTrue(fileNumber).isPresent()) {
            log.warn("上传文档失败 - 文件编号已存在: {}", fileNumber);
            throw new BizException(ErrorCode.FILE_NUMBER_EXISTS);
        }

        // 保存文件
        log.debug("上传文档 - 开始保存文件");
        String objectKey = saveFile(file, fileType);
        log.info("上传文档 - 文件保存成功, objectKey: {}", objectKey);
        
        // 生成缩略图
        log.debug("上传文档 - 开始生成缩略图");
        String thumbnailPath = generateThumbnail(file, objectKey);
        if (thumbnailPath != null) {
            log.debug("上传文档 - 缩略图生成成功: {}", thumbnailPath);
        } else {
            log.debug("上传文档 - 未生成缩略图（非图片类型）");
        }
        
        // 创建文档实体
        Document document = new Document();
        document.setFileNumber(fileNumber);
        document.setFileName(fileName);
        document.setOriginalName(originalFilename);
        document.setProductModel(productModel);
        document.setVersion(version);
        document.setCompileDate(compileDate);
        document.setCompilerId(userId);
        document.setCompilerName(SecurityContext.getCurrentUserName());
        document.setDescription(description);
        document.setFileType(fileType);
        document.setFileSize(file.getSize());
        document.setFilePath(objectKey);
        document.setThumbnailPath(thumbnailPath);
        document.setContentType(file.getContentType());
        document.setStatus(DocumentStatus.DRAFT);
        document.setIsCurrentVersion(true);
        document.setCreateUserId(userId);
        document.setUpdateUserId(userId);
        
        Document saved = documentRepository.save(document);
        log.info("上传文档成功 - 文档ID: {}, 文件编号: {}, 文件名称: {}", 
                saved.getId(), saved.getFileNumber(), saved.getFileName());
        log.debug("=== DocumentService.uploadDocument 结束 ===");
        return saved;
    }

    /**
     * 保存文件
     */
    private String saveFile(MultipartFile file, FileType fileType) throws IOException {

        // ⭐ 项目启动目录（不是 Tomcat）
        String projectDir = System.getProperty("user.dir");

        Path uploadPath = Paths.get(
                projectDir,
                uploadDir,
                fileType.name().toLowerCase()
        );

        log.info("saveFile projectDir:{}, uploadPath: {}", projectDir, uploadPath);

        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + "." + extension;

        Path filePath = uploadPath.resolve(uniqueFilename);

        // ⭐ 现在 transferTo 写的是真实磁盘路径
        file.transferTo(filePath.toFile());

        return fileType.name().toLowerCase() + "/" + uniqueFilename;
    }

    /**
     * 生成缩略图
     */
    private String generateThumbnail(MultipartFile file, String objectKey) {
        try {
            FileType fileType = FileType.fromExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
            
            // 只对图片类型生成缩略图
            if (fileType != FileType.JPEG && fileType != FileType.PNG) {
                return null;
            }

            Path thumbnailPath = Paths.get(uploadDir, "thumbnails");
            Files.createDirectories(thumbnailPath);

            String thumbnailFilename = "thumb_" + UUID.randomUUID().toString() + ".jpg";
            Path thumbnailFile = thumbnailPath.resolve(thumbnailFilename);

            Thumbnails.of(file.getInputStream())
                    .size(200, 200)
                    .outputFormat("jpg")
                    .toFile(thumbnailFile.toFile());

            return "thumbnails/" + thumbnailFilename;
        } catch (Exception e) {
            log.warn("生成缩略图失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 根据ID查询文档
     */
    public Document getDocumentById(Long id) {
        log.debug("=== DocumentService.getDocumentById 开始 ===");
        log.info("查询文档 - 文档ID: {}", id);
        
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("查询文档失败 - 文档不存在: {}", id);
                    return new BizException(ErrorCode.DOCUMENT_NOT_FOUND);
                });
        
        log.info("查询文档成功 - 文档ID: {}, 文件编号: {}, 文件名称: {}", 
                document.getId(), document.getFileNumber(), document.getFileName());
        log.debug("=== DocumentService.getDocumentById 结束 ===");
        return document;
    }
}

