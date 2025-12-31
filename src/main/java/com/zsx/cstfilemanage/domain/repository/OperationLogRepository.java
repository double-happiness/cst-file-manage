package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.OperationLog;
import com.zsx.cstfilemanage.domain.cenum.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志仓储接口
 */
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    /**
     * 根据用户ID查询
     */
    Page<OperationLog> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据操作类型查询
     */
    Page<OperationLog> findByOperationType(OperationType operationType, Pageable pageable);

    /**
     * 多条件查询日志
     */
    @Query("""
            SELECT ol FROM OperationLog ol
            WHERE
            (:userId IS NULL OR ol.userId = :userId)
            AND
            (:operationType IS NULL OR ol.operationType = :operationType)
            AND
            (:startTime IS NULL OR ol.createTime >= :startTime)
            AND
            (:endTime IS NULL OR ol.createTime < :endTime)
            """)
    Page<OperationLog> searchLogs(
            @Param("userId") Long userId,
            @Param("operationType") OperationType operationType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );


    /**
     * 查询指定时间之前的日志（用于清理）
     */
    @Query("SELECT ol FROM OperationLog ol WHERE ol.createTime < :beforeTime")
    List<OperationLog> findLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);
}

