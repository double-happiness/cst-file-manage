package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.model.entity.FileEntity;
import com.zsx.cstfilemanage.domain.model.entity.UploadSession;
import com.zsx.cstfilemanage.domain.policy.FileTypePolicyManager;
import com.zsx.cstfilemanage.domain.repository.UploadSessionRepository;
import com.zsx.cstfilemanage.domain.storage.OcsClient;
import com.zsx.cstfilemanage.domain.storage.UploadToken;
import com.zsx.cstfilemanage.infrastructure.security.SecurityContext;
import com.zsx.cstfilemanage.interfaces.http.request.UploadInitRequest;
import com.zsx.cstfilemanage.interfaces.http.response.UploadInitResponse;
import org.springframework.stereotype.Service;

@Service
public class UploadInitAppService {

    private final FileTypePolicyManager policyManager;
    private final OcsClient ocsClient;
    private final UploadSessionRepository uploadSessionRepository;

    public UploadInitAppService(FileTypePolicyManager policyManager,
                                OcsClient ocsClient,
                                UploadSessionRepository uploadSessionRepository) {
        this.policyManager = policyManager;
        this.ocsClient = ocsClient;
        this.uploadSessionRepository = uploadSessionRepository;
    }

    public UploadInitResponse initUpload(UploadInitRequest req) {
        System.out.println("initUpload req:" + req);
        // //获取当前用户（示例）
        // Long userId = SecurityContext.getCurrentUserId();
        // if (userId == null) {
        //     throw new BizException(ErrorCode.UNAUTHORIZED);
        // }

        // // 文件策略校验（领域规则）
        // policyManager.validate(
        //         req.getBizType(),
        //         req.getContentType(),
        //         req.getFileSize()
        // );

        // 生成 uploadId
        String uploadId = UploadSession.generateUploadId();

        // 生成 objectKey（领域规则）
        String objectKey = FileEntity.generateObjectKey(
                req.getBizType(), req.getFileName()
        );

        // 生成对象存储上传凭证
        UploadToken token =
                ocsClient.generateUploadToken(objectKey, 15 * 60);

        // // 创建 UploadSession（领域对象）
        // UploadSession session = UploadSession.init(
        //         uploadId,
        //         userId,
        //         objectKey,
        //         req.getBizType()
        // );

        // 保存上传会话（Redis）
        // uploadSessionRepository.save(session);

        // 返回
        return new UploadInitResponse(
                uploadId,
                token.getUploadUrl(),
                objectKey,
                token.getExpireAt(),
                "DIRECT"
        );
    }
}
