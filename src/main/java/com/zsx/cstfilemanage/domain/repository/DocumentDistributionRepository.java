package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.DocumentDistribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 文件下发记录仓储接口
 */
public interface DocumentDistributionRepository extends JpaRepository<DocumentDistribution, Long> {

    /**
     * 根据文档ID查询下发记录
     */
    List<DocumentDistribution> findByDocumentId(Long documentId);

    /**
     * 根据下发人ID查询
     */
    Page<DocumentDistribution> findByDistributorId(Long distributorId, Pageable pageable);
}

