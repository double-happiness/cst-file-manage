package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.LogExportService;
import com.zsx.cstfilemanage.application.service.OperationLogService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.cenum.OperationType;
import com.zsx.cstfilemanage.domain.model.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/v1/logs")
public class OperationLogController {

    private final OperationLogService operationLogService;
    private final LogExportService logExportService;

    public OperationLogController(OperationLogService operationLogService,
                                 LogExportService logExportService) {
        this.operationLogService = operationLogService;
        this.logExportService = logExportService;
    }

    /**
     * 查询日志
     */
    @GetMapping
    public ApiResponse<Page<OperationLog>> searchLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) OperationType operationType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OperationLog> logs = operationLogService.searchLogs(userId, operationType, startTime, endTime, pageable);
        return ApiResponse.success(logs);
    }

    /**
     * 导出日志为Excel
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) OperationType operationType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) throws IOException {
        byte[] excelBytes = logExportService.exportLogsToExcel(userId, operationType, startTime, endTime);
        
        String filename = "操作日志_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(excelBytes);
    }
}

