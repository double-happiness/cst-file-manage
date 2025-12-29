package com.zsx.cstfilemanage.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件下发接收人记录
 */
@Entity
@Table(name = "distribution_receivers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"distributionId", "receiverId"})
})
@Data
public class DistributionReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 下发记录ID
     */
    @Column(nullable = false)
    private Long distributionId;

    /**
     * 接收人ID
     */
    @Column(nullable = false)
    private Long receiverId;

    /**
     * 接收人姓名
     */
    @Column(nullable = false, length = 100)
    private String receiverName;

    /**
     * 是否已查看
     */
    @Column(nullable = false)
    private Boolean viewed = false;

    /**
     * 查看时间
     */
    @Column
    private LocalDateTime viewTime;

    /**
     * 是否已下载
     */
    @Column(nullable = false)
    private Boolean downloaded = false;

    /**
     * 下载时间
     */
    @Column
    private LocalDateTime downloadTime;

    @PrePersist
    protected void onCreate() {
        viewed = false;
        downloaded = false;
    }
}

