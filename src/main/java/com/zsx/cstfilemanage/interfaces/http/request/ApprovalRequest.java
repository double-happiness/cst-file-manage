package com.zsx.cstfilemanage.interfaces.http.request;

import com.zsx.cstfilemanage.domain.cenum.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批请求
 */
@Data
public class ApprovalRequest {

    @NotNull(message = "审批状态不能为空")
    private ApprovalStatus status;

    private String comment;
}

