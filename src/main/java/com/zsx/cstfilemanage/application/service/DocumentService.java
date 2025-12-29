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
        // 获取当前用户
        Long userId = SecurityContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        // 验证文件格式
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        FileType fileType = FileType.fromExtension(extension);
        
        if (!fileTypeValidator.isAllowed(fileType, extension)) {
            throw new BizException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }

        // 检查文件编号是否已存在
        if (documentRepository.findByFileNumberAndIsCurrentVersionTrue(fileNumber).isPresent()) {
            throw new BizException(new ErrorCode(1005, "文件编号已存在"));
        }

        // 保存文件
        String objectKey = saveFile(file, fileType);
        
        // 生成缩略图
        String thumbnailPath = generateThumbnail(file, objectKey);

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

        return documentRepository.save(document);
    }

    /**
     * 保存文件
     */
    private String saveFile(MultipartFile file, FileType fileType) throws IOException {
        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir, fileType.name().toLowerCase());
        Files.createDirectories(uploadPath);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // 保存文件
        file.transferTo(filePath.toFile());

        // 返回相对路径作为objectKey
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
        return documentRepository.findById(id)
                .orElseThrow(() -> new BizException(new ErrorCode(1006, "文档不存在")));
    }
}

