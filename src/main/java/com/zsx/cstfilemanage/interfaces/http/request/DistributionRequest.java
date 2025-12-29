package com.zsx.cstfilemanage.interfaces.http.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件下发请求
 */
@Data
public class DistributionRequest {

    @NotEmpty(message = "文档ID列表不能为空")
    private List<Long> documentIds;

    @NotNull(message = "下发对象类型不能为空")
    private String targetType; // DEPARTMENT, POSITION, USER_GROUP, USER

    @NotEmpty(message = "下发对象ID列表不能为空")
    private List<Long> targetIds;

    @NotEmpty(message = "下发对象名称列表不能为空")
    private List<String> targetNames;

    private String distributionNote;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime effectiveDate;
}

