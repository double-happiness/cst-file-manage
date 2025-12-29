package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.util.JsonUtil;
import com.zsx.cstfilemanage.config.FileStorageProperties;
import com.zsx.cstfilemanage.domain.cenum.UploadStatus;
import com.zsx.cstfilemanage.domain.model.entity.UploadSession;
import com.zsx.cstfilemanage.domain.storage.OcsClient;
import com.zsx.cstfilemanage.domain.storage.UploadToken;
import com.zsx.cstfilemanage.infrastructure.security.SecurityContext;
import com.zsx.cstfilemanage.interfaces.http.request.UploadInitRequest;
import com.zsx.cstfilemanage.interfaces.http.response.UploadInitResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.zsx.cstfilemanage.common.exception.ErrorCode.FILE_SIZE_INVALID;

@Service
public class FileStorageService {
    private static final long MAX_FILE_SIZE = 500 * 1024 * 1024; // 500MB

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private OcsClient ocsClient;

    public UploadInitResponse initUpload(UploadInitRequest req) {

        // 1️⃣ 登录 & 权限校验
        Long userId = SecurityContext.getCurrentUserId();
        Assert.notNull(userId, "unauthorized");

        // 2️⃣ 文件大小校验
        if (req.getFileSize() <= 0 || req.getFileSize() > MAX_FILE_SIZE) {
            throw new BizException(FILE_SIZE_INVALID);
        }

        // 3️⃣ 文件类型校验
        // FileTypeValidator.validate(req.getContentType(), req.getBizType());

        // 4️⃣ 频率控制（防刷）
        limitUploadRate(userId);

        // 5️⃣ 生成 uploadId
        String uploadId = "upl_" + UUID.randomUUID().toString().replace("-", "");

        // 6️⃣ 生成 objectKey（服务端控制）
        String objectKey = buildObjectKey(req.getBizType(), req.getFileName());

        // 7️⃣ 生成上传凭证
        UploadToken token = ocsClient.generateUploadToken(objectKey, 15 * 60);

        // 8️⃣ Redis 记录上传状态
        UploadSession session = new UploadSession(
                uploadId, userId, objectKey, req.getBizType(), UploadStatus.INIT, System.currentTimeMillis()
        );

        redisTemplate.opsForValue().set(
                redisKey(uploadId),
                JsonUtil.toJson(session),
                15, TimeUnit.MINUTES
        );

        // 9️⃣ 返回
        return new UploadInitResponse(
                uploadId,
                token.getUploadUrl(),
                objectKey,
                token.getExpireAt(),
                "DIRECT"
        );
    }

    private void limitUploadRate(Long userId) {
        String key = "upload:rate:" + userId;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }
        if (count > 20) {
            throw new BizException(FILE_SIZE_INVALID);
        }
    }

    private String buildObjectKey(String bizType, String fileName) {
        String ext = FilenameUtils.getExtension(fileName);
        return String.format("%s/%s/%s.%s",
                bizType,
                LocalDate.now(),
                UUID.randomUUID(),
                ext
        );
    }

    private String redisKey(String uploadId) {
        return "upload:init:" + uploadId;
    }

    private final Path fileStorageLocation;

    public FileStorageService(FileStorageProperties properties) {
        this.fileStorageLocation = Paths.get(properties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建上传目录", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 清理文件名
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("文件名包含非法路径: " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            System.out.printf("file-------%s\n", targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("保存文件失败: " + fileName, ex);
        }
    }

}
