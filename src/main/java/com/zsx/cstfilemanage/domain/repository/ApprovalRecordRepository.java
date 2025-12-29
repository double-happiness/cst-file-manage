package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.ApprovalRecord;
import com.zsx.cstfilemanage.domain.cenum.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 审批记录仓储接口
 */
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {

    /**
     * 根据文档ID查询所有审批记录
     */
    List<ApprovalRecord> findByDocumentIdOrderByStepOrderAsc(Long documentId);

    /**
     * 根据文档ID和审批人ID查询待审批记录
     */
    @Query("SELECT ar FROM ApprovalRecord ar WHERE ar.documentId = :documentId " +
           "AND ar.approverId = :approverId AND ar.status = :status")
    Optional<ApprovalRecord> findPendingByDocumentAndApprover(
            @Param("documentId") Long documentId,
            @Param("approverId") Long approverId,
            @Param("status") ApprovalStatus status
    );

    /**
     * 查询指定文档的当前审批环节
     */
    @Query("SELECT ar FROM ApprovalRecord ar WHERE ar.documentId = :documentId " +
           "AND ar.status = :status ORDER BY ar.stepOrder ASC")
    List<ApprovalRecord> findCurrentStepRecords(
            @Param("documentId") Long documentId,
            @Param("status") ApprovalStatus status
    );

    /**
     * 查询用户的待审批记录
     */
    List<ApprovalRecord> findByApproverIdAndStatus(Long approverId, ApprovalStatus status);
}

