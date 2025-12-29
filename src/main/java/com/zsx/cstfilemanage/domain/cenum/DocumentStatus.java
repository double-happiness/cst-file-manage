package com.zsx.cstfilemanage.domain.cenum;

/**
 * 文档状态枚举
 */
public enum DocumentStatus {
    DRAFT("草稿"),
    PENDING_APPROVAL("待审批"),
    APPROVING("审批中"),
    APPROVED("已批准"),
    REJECTED("已驳回"),
    RECALLED("已回收"),
    OBSOLETE("已作废");

    private final String description;

    DocumentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

