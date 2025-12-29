package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.cenum.FileType;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.domain.repository.DocumentRepository;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件对比服务
 */
@Service
@Slf4j
public class FileCompareService {

    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public FileCompareService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * 对比两个文档
     */
    public FileCompareResult compareDocuments(Long version1Id, Long version2Id) throws IOException {
        Document version1 = documentRepository.findById(version1Id)
                .orElseThrow(() -> new BizException(new ErrorCode(1006, "版本1不存在")));
        Document version2 = documentRepository.findById(version2Id)
                .orElseThrow(() -> new BizException(new ErrorCode(1006, "版本2不存在")));

        FileCompareResult result = new FileCompareResult();
        result.setVersion1(version1);
        result.setVersion2(version2);

        // 根据文件类型选择对比方式
        if (version1.getFileType() == FileType.PDF && version2.getFileType() == FileType.PDF) {
            result = comparePdfFiles(version1, version2);
        } else if (isTextFile(version1.getFileType()) && isTextFile(version2.getFileType())) {
            result = compareTextFiles(version1, version2);
        } else {
            result.setDifferences("文件类型不支持对比，或两个文件类型不一致");
            result.setHasDifferences(false);
        }

        return result;
    }

    /**
     * 对比PDF文件
     */
    private FileCompareResult comparePdfFiles(Document version1, Document version2) throws IOException {
        FileCompareResult result = new FileCompareResult();
        result.setVersion1(version1);
        result.setVersion2(version2);

        try {
            Path path1 = Paths.get(uploadDir, version1.getFilePath());
            Path path2 = Paths.get(uploadDir, version2.getFilePath());

            String text1 = extractPdfText(path1.toFile());
            String text2 = extractPdfText(path2.toFile());

            if (text1.equals(text2)) {
                result.setHasDifferences(false);
                result.setDifferences("两个PDF文件内容相同");
            } else {
                result.setHasDifferences(true);
                result.setDifferences(compareText(text1, text2));
            }
        } catch (Exception e) {
            log.error("PDF对比失败", e);
            result.setHasDifferences(true);
            result.setDifferences("PDF对比失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 对比文本文件
     */
    private FileCompareResult compareTextFiles(Document version1, Document version2) throws IOException {
        FileCompareResult result = new FileCompareResult();
        result.setVersion1(version1);
        result.setVersion2(version2);

        try {
            Path path1 = Paths.get(uploadDir, version1.getFilePath());
            Path path2 = Paths.get(uploadDir, version2.getFilePath());

            String text1 = Files.readString(path1);
            String text2 = Files.readString(path2);

            if (text1.equals(text2)) {
                result.setHasDifferences(false);
                result.setDifferences("两个文件内容相同");
            } else {
                result.setHasDifferences(true);
                result.setDifferences(compareText(text1, text2));
            }
        } catch (Exception e) {
            log.error("文本对比失败", e);
            result.setHasDifferences(true);
            result.setDifferences("文本对比失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 提取PDF文本
     */
    private String extractPdfText(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * 对比文本内容
     */
    private String compareText(String text1, String text2) {
        List<String> lines1 = List.of(text1.split("\n"));
        List<String> lines2 = List.of(text2.split("\n"));

        Patch<String> patch = DiffUtils.diff(lines1, lines2);
        List<AbstractDelta<String>> deltas = patch.getDeltas();

        if (deltas.isEmpty()) {
            return "文件内容相同";
        }

        StringBuilder diff = new StringBuilder();
        diff.append("发现 ").append(deltas.size()).append(" 处差异：\n\n");

        for (AbstractDelta<String> delta : deltas) {
            diff.append("位置: ").append(delta.getSource().getPosition()).append("\n");
            diff.append("类型: ").append(delta.getType()).append("\n");
            diff.append("原文: ").append(String.join("\n", delta.getSource().getLines())).append("\n");
            diff.append("修改后: ").append(String.join("\n", delta.getTarget().getLines())).append("\n");
            diff.append("---\n");
        }

        return diff.toString();
    }

    /**
     * 判断是否为文本文件
     */
    private boolean isTextFile(FileType fileType) {
        return fileType == FileType.WORD || fileType == FileType.EXCEL;
    }

    /**
     * 文件对比结果
     */
    public static class FileCompareResult {
        private Document version1;
        private Document version2;
        private boolean hasDifferences;
        private String differences;

        // Getters and Setters
        public Document getVersion1() { return version1; }
        public void setVersion1(Document version1) { this.version1 = version1; }
        public Document getVersion2() { return version2; }
        public void setVersion2(Document version2) { this.version2 = version2; }
        public boolean isHasDifferences() { return hasDifferences; }
        public void setHasDifferences(boolean hasDifferences) { this.hasDifferences = hasDifferences; }
        public String getDifferences() { return differences; }
        public void setDifferences(String differences) { this.differences = differences; }
    }
}

