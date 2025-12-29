package com.zsx.cstfilemanage.domain.cenum;

/**
 * 审批状态枚举
 */
public enum ApprovalStatus {
    PENDING("待审批"),
    APPROVED("同意"),
    REJECTED("驳回"),
    MODIFY_REQUIRED("修改后再审");

    private final String description;

    ApprovalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

