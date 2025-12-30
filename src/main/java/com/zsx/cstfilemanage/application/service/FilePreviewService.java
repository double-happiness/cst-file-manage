package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.cenum.FileType;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.domain.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件预览服务
 */
@Service
@Slf4j
public class FilePreviewService {

    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public FilePreviewService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * 获取文件预览资源
     */
    public Resource getPreviewResource(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BizException(ErrorCode.DOCUMENT_NOT_FOUND));

        Path filePath = Paths.get(uploadDir, document.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new BizException(ErrorCode.FILE_NOT_FOUND);
        }

        return resource;
    }

    /**
     * 获取PDF第一页预览图
     */
    public byte[] getPdfPreview(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BizException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (document.getFileType() != FileType.PDF) {
            throw new BizException(ErrorCode.NOT_PDF_FILE);
        }

        Path filePath = Paths.get(uploadDir, document.getFilePath());
        File pdfFile = filePath.toFile();

        try (PDDocument document_pdf = Loader.loadPDF(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document_pdf);
            
            // 渲染第一页为图片
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 150);
            
            // 转换为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        }
    }

    /**
     * 获取图片预览
     */
    public Resource getImagePreview(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BizException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (document.getFileType() != FileType.JPEG && document.getFileType() != FileType.PNG) {
            throw new BizException(ErrorCode.NOT_IMAGE_FILE);
        }

        return getPreviewResource(documentId);
    }

    /**
     * 检查文件是否支持预览
     */
    public boolean isPreviewSupported(FileType fileType) {
        return fileType == FileType.PDF 
            || fileType == FileType.JPEG 
            || fileType == FileType.PNG
            || fileType == FileType.WORD
            || fileType == FileType.EXCEL;
    }
}

