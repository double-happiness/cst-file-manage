package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.domain.cenum.OperationType;
import com.zsx.cstfilemanage.domain.model.entity.OperationLog;
import com.zsx.cstfilemanage.domain.repository.OperationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 日志导出服务
 */
@Service
@Slf4j
public class LogExportService {

    private final OperationLogRepository operationLogRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogExportService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    /**
     * 导出日志为Excel
     */
    public byte[] exportLogsToExcel(Long userId,
                                   OperationType operationType,
                                   LocalDateTime startTime,
                                   LocalDateTime endTime) throws IOException {
        // 查询所有符合条件的日志（不分页）
        List<OperationLog> logs = operationLogRepository.searchLogs(
                userId, operationType, startTime, endTime, Pageable.unpaged()
        ).getContent();

        // 创建Excel工作簿
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("操作日志");

            // 创建标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "操作人", "操作类型", "操作内容", "操作对象类型", "操作对象ID", 
                              "操作结果", "错误信息", "IP地址", "操作时间"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 创建数据行样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 填充数据
            int rowNum = 1;
            for (OperationLog log : logs) {
                Row row = sheet.createRow(rowNum++);
                
                createCell(row, 0, log.getId().toString(), dataStyle);
                createCell(row, 1, log.getUserName(), dataStyle);
                createCell(row, 2, log.getOperationType().getDescription(), dataStyle);
                createCell(row, 3, log.getOperationContent(), dataStyle);
                createCell(row, 4, log.getObjectType(), dataStyle);
                createCell(row, 5, log.getObjectId() != null ? log.getObjectId().toString() : "", dataStyle);
                createCell(row, 6, log.getResult(), dataStyle);
                createCell(row, 7, log.getErrorMessage(), dataStyle);
                createCell(row, 8, log.getIpAddress(), dataStyle);
                createCell(row, 9, log.getCreateTime().format(DATE_FORMATTER), dataStyle);
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 设置最小列宽
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 3000));
            }

            // 写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 创建单元格
     */
    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
}

