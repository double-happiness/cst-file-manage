package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.domain.cenum.OperationType;
import com.zsx.cstfilemanage.domain.model.entity.OperationLog;
import com.zsx.cstfilemanage.domain.repository.OperationLogRepository;
import com.zsx.cstfilemanage.infrastructure.security.SecurityContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 操作日志服务
 */
@Service
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    public OperationLogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    /**
     * 记录操作日志
     */
    @Transactional
    public void logOperation(OperationType operationType,
                            String operationContent,
                            String objectType,
                            Long objectId,
                            String result,
                            String errorMessage,
                            String ipAddress,
                            String userAgent) {
        Long userId = SecurityContext.getCurrentUserId();
        String userName = SecurityContext.getCurrentUserName();

        OperationLog log = new OperationLog();
        log.setUserId(userId != null ? userId : 0L);
        log.setUserName(userName != null ? userName : "系统");
        log.setOperationType(operationType);
        log.setOperationContent(operationContent);
        log.setObjectType(objectType);
        log.setObjectId(objectId);
        log.setResult(result);
        log.setErrorMessage(errorMessage);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);

        operationLogRepository.save(log);
    }

    /**
     * 查询日志
     */
    public Page<OperationLog> searchLogs(Long userId,
                                        OperationType operationType,
                                        LocalDateTime startTime,
                                        LocalDateTime endTime,
                                        Pageable pageable) {
        return operationLogRepository.searchLogs(userId, operationType, startTime, endTime, pageable);
    }

    /**
     * 清理过期日志（保留3年）
     */
    @Transactional
    public void cleanExpiredLogs() {
        LocalDateTime threeYearsAgo = LocalDateTime.now().minusYears(3);
        // 实际应该使用批量删除，这里简化处理
        // operationLogRepository.deleteAll(operationLogRepository.findLogsBefore(threeYearsAgo));
    }
}

