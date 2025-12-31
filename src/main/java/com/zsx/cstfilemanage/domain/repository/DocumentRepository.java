package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 文档仓储接口
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * 根据文件编号查询
     */
    Optional<Document> findByFileNumber(String fileNumber);

    /**
     * 根据文件编号和版本查询
     */
    Optional<Document> findByFileNumberAndVersion(String fileNumber, String version);

    /**
     * 查询指定文件编号的所有版本
     */
    List<Document> findByFileNumberOrderByVersionDesc(String fileNumber);

    /**
     * 查询当前有效版本
     */
    Optional<Document> findByFileNumberAndIsCurrentVersionTrue(String fileNumber);

    /**
     * 根据状态查询
     */
    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);

    /**
     * 根据创建人查询
     */
    Page<Document> findByCreateUserId(Long userId, Pageable pageable);

    /**
     * 根据产品型号查询
     */
    Page<Document> findByProductModel(String productModel, Pageable pageable);

    /**
     * 多条件搜索
     */
    @Query("""
            SELECT d FROM Document d
            WHERE
            (:fileNumber IS NULL OR :fileNumber = '' OR d.fileNumber LIKE CONCAT('%', :fileNumber, '%'))
            AND
            (:fileName IS NULL OR :fileName = '' OR d.fileName LIKE CONCAT('%', :fileName, '%'))
            AND
            (:productModel IS NULL OR :productModel = '' OR d.productModel = :productModel)
            AND
            (:status IS NULL OR d.status = :status)
            AND
            (:compilerId IS NULL OR d.compilerId = :compilerId)
            """)
    Page<Document> searchDocuments(
            @Param("fileNumber") String fileNumber,
            @Param("fileName") String fileName,
            @Param("productModel") String productModel,
            @Param("status") DocumentStatus status,
            @Param("compilerId") Long compilerId,
            Pageable pageable
    );

    /**
     * 查询待审批的文档
     */
    @Query("SELECT d FROM Document d WHERE d.status = :status")
    List<Document> findPendingApprovalDocuments(@Param("status") DocumentStatus status);
}

