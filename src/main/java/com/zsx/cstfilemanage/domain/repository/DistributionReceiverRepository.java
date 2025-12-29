package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.DistributionReceiver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 文件下发接收人仓储接口
 */
public interface DistributionReceiverRepository extends JpaRepository<DistributionReceiver, Long> {

    /**
     * 根据下发记录ID查询接收人
     */
    List<DistributionReceiver> findByDistributionId(Long distributionId);

    /**
     * 根据接收人ID查询
     */
    List<DistributionReceiver> findByReceiverId(Long receiverId);

    /**
     * 根据下发记录ID和接收人ID查询
     */
    Optional<DistributionReceiver> findByDistributionIdAndReceiverId(Long distributionId, Long receiverId);
}

