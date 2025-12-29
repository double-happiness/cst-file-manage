package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.ApprovalFlow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 审批流程仓储接口
 */
public interface ApprovalFlowRepository extends JpaRepository<ApprovalFlow, Long> {

    /**
     * 查询启用的审批流程
     */
    List<ApprovalFlow> findByEnabledTrue();

    /**
     * 根据流程名称查询
     */
    Optional<ApprovalFlow> findByFlowName(String flowName);
}

