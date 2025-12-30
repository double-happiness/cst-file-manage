package com.zsx.cstfilemanage.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.domain.model.entity.DocumentDistribution;
import com.zsx.cstfilemanage.domain.model.entity.DistributionReceiver;
import com.zsx.cstfilemanage.domain.model.entity.User;
import com.zsx.cstfilemanage.domain.repository.DocumentDistributionRepository;
import com.zsx.cstfilemanage.domain.repository.DocumentRepository;
import com.zsx.cstfilemanage.domain.repository.DistributionReceiverRepository;
import com.zsx.cstfilemanage.domain.repository.UserRepository;
import com.zsx.cstfilemanage.domain.repository.UserGroupMemberRepository;
import com.zsx.cstfilemanage.application.service.NotificationService;
import com.zsx.cstfilemanage.infrastructure.security.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件下发服务
 */
@Service
@Slf4j
public class DistributionService {

    private final DocumentRepository documentRepository;
    private final DocumentDistributionRepository distributionRepository;
    private final DistributionReceiverRepository receiverRepository;
    private final UserRepository userRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public DistributionService(DocumentRepository documentRepository,
                              DocumentDistributionRepository distributionRepository,
                              DistributionReceiverRepository receiverRepository,
                              UserRepository userRepository,
                              UserGroupMemberRepository userGroupMemberRepository,
                              NotificationService notificationService) {
        this.documentRepository = documentRepository;
        this.distributionRepository = distributionRepository;
        this.receiverRepository = receiverRepository;
        this.userRepository = userRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
        this.notificationService = notificationService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 下发文件
     */
    @Transactional
    public void distributeDocuments(List<Long> documentIds,
                                   String targetType,
                                   List<Long> targetIds,
                                   List<String> targetNames,
                                   String distributionNote,
                                   LocalDateTime effectiveDate) {
        Long distributorId = SecurityContext.getCurrentUserId();
        if (distributorId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        User distributor = userRepository.findById(distributorId)
                .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));

        // 验证文档状态
        for (Long documentId : documentIds) {
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new BizException(ErrorCode.DOCUMENT_NOT_FOUND));
            
            if (document.getStatus() != DocumentStatus.APPROVED) {
                throw new BizException(ErrorCode.DOCUMENT_NOT_APPROVED);
            }
            
            if (!document.getIsCurrentVersion()) {
                throw new BizException(ErrorCode.NOT_CURRENT_VERSION);
            }
        }

        // 创建下发记录
        for (Long documentId : documentIds) {
            DocumentDistribution distribution = new DocumentDistribution();
            distribution.setDocumentId(documentId);
            distribution.setDistributorId(distributorId);
            distribution.setDistributorName(distributor.getRealName());
            distribution.setDistributionNote(distributionNote);
            distribution.setEffectiveDate(effectiveDate);
            distribution.setTargetType(targetType);
            
            try {
                distribution.setTargetIds(objectMapper.writeValueAsString(targetIds));
                distribution.setTargetNames(objectMapper.writeValueAsString(targetNames));
            } catch (Exception e) {
                throw new BizException(ErrorCode.DISTRIBUTION_TARGET_ERROR);
            }

            distribution = distributionRepository.save(distribution);

            // 创建接收人记录
            List<Long> receiverIds = getReceiverIds(targetType, targetIds);
            List<User> receivers = new ArrayList<>();
            for (Long receiverId : receiverIds) {
                User receiver = userRepository.findById(receiverId)
                        .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));
                
                DistributionReceiver receiverRecord = new DistributionReceiver();
                receiverRecord.setDistributionId(distribution.getId());
                receiverRecord.setReceiverId(receiverId);
                receiverRecord.setReceiverName(receiver.getRealName());
                receiverRepository.save(receiverRecord);
                receivers.add(receiver);
            }
            
            // 发送下发通知
            Document doc = documentRepository.findById(documentId).orElse(null);
            if (doc != null) {
                notificationService.sendDistributionNotification(receivers, doc, distributor.getRealName());
            }
        }
    }

    /**
     * 回收文件
     */
    @Transactional
    public void recallDocuments(List<Long> documentIds) {
        Long userId = SecurityContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        for (Long documentId : documentIds) {
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new BizException(ErrorCode.DOCUMENT_NOT_FOUND));
            
            document.setStatus(DocumentStatus.RECALLED);
            documentRepository.save(document);
        }
    }

    /**
     * 作废文件
     */
    @Transactional
    public void obsoleteDocuments(List<Long> documentIds) {
        Long userId = SecurityContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        for (Long documentId : documentIds) {
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new BizException(ErrorCode.DOCUMENT_NOT_FOUND));
            
            document.setStatus(DocumentStatus.OBSOLETE);
            documentRepository.save(document);
        }
    }

    /**
     * 记录文件查看
     */
    @Transactional
    public void recordView(Long distributionId) {
        Long userId = SecurityContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        DistributionReceiver receiver = receiverRepository
                .findByDistributionIdAndReceiverId(distributionId, userId)
                .orElse(null);

        if (receiver != null && !receiver.getViewed()) {
            receiver.setViewed(true);
            receiver.setViewTime(LocalDateTime.now());
            receiverRepository.save(receiver);
        }
    }

    /**
     * 记录文件下载
     */
    @Transactional
    public void recordDownload(Long distributionId) {
        Long userId = SecurityContext.getCurrentUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        DistributionReceiver receiver = receiverRepository
                .findByDistributionIdAndReceiverId(distributionId, userId)
                .orElse(null);

        if (receiver != null && !receiver.getDownloaded()) {
            receiver.setDownloaded(true);
            receiver.setDownloadTime(LocalDateTime.now());
            receiverRepository.save(receiver);
        }
    }

    /**
     * 获取接收人ID列表
     */
    private List<Long> getReceiverIds(String targetType, List<Long> targetIds) {
        List<Long> receiverIds = new ArrayList<>();
        
        switch (targetType) {
            case "USER":
                receiverIds.addAll(targetIds);
                break;
            case "DEPARTMENT":
                // 查询部门下的所有用户
                for (Long deptId : targetIds) {
                    List<User> users = userRepository.findByDepartmentId(deptId);
                    users.forEach(user -> receiverIds.add(user.getId()));
                }
                break;
            case "POSITION":
                // TODO: 根据岗位查询用户（需要添加岗位字段到User实体）
                break;
            case "USER_GROUP":
                // 根据用户组查询用户
                for (Long groupId : targetIds) {
                    List<Long> userIds = userGroupMemberRepository.findUserIdsByGroupId(groupId);
                    receiverIds.addAll(userIds);
                }
                break;
        }
        
        return receiverIds;
    }
}

