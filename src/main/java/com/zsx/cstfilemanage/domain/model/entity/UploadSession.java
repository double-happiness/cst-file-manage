package com.zsx.cstfilemanage.domain.model.entity;

import com.zsx.cstfilemanage.domain.cenum.UploadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadSession {

    private String uploadId;
    private Long userId;
    private String objectKey;
    private String bizType;
    private UploadStatus status;
    private Long createTime;

    public static UploadSession init(
            String uploadId,
            Long userId,
            String objectKey,
            String bizType) {

        return new UploadSession(
                uploadId,
                userId,
                objectKey,
                bizType,
                UploadStatus.INIT,
                System.currentTimeMillis()
        );
    }

    public static String generateUploadId() {
        return "upl_" + UUID.randomUUID().toString().replace("-", "");
    }
}
