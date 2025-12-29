package com.zsx.cstfilemanage.interfaces.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 创建版本请求
 */
@Data
public class CreateVersionRequest {

    @NotBlank(message = "新版本号不能为空")
    private String newVersion;

    private String changeDescription;

    private String changeReason;

    @NotNull(message = "修改日期不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime changeDate;
}

