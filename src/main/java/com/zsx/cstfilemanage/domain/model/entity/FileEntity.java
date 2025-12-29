package com.zsx.cstfilemanage.domain.model.entity;

import lombok.Data;
import org.apache.commons.io.FilenameUtils;

import java.time.LocalDateTime;

@Data
public class FileEntity {

    private String uploadId;     // 上传唯一 ID
    private String originalName; // 原始文件名
    private String objectKey;    // 对象存储 key
    private String bizType;      // 业务类型：image/video/audio/ppt/pdf
    private String contentType;  // MIME 类型
    private Long fileSize;       // 文件大小
    private String status;       // 上传状态：INIT / UPLOADING / DONE
    private LocalDateTime createTime;

    /**
     * 生成对象存储 key（默认策略）
     */
    public static String generateObjectKey(String bizType, String fileName) {
        String ext = FilenameUtils.getExtension(fileName);
        String baseName = FilenameUtils.removeExtension(fileName);
        return String.format("%s/%s_%d.%s",
                bizType,
                baseName,
                System.currentTimeMillis(),
                ext);
    }

    /**
     * 初始化文件实体
     */
    public static FileEntity init(String uploadId, String originalName, String bizType, String contentType, Long fileSize) {
        FileEntity entity = new FileEntity();
        entity.setUploadId(uploadId);
        entity.setOriginalName(originalName);
        entity.setBizType(bizType);
        entity.setContentType(contentType);
        entity.setFileSize(fileSize);
        entity.setObjectKey(generateObjectKey(bizType, originalName));
        entity.setStatus("INIT");
        entity.setCreateTime(LocalDateTime.now());
        return entity;
    }
}
