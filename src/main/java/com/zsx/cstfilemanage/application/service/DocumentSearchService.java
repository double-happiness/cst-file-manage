package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.domain.repository.DocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 文档搜索服务
 */
@Service
public class DocumentSearchService {

    private final DocumentRepository documentRepository;

    public DocumentSearchService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * 搜索文档
     */
    public Page<Document> searchDocuments(String fileNumber,
                                        String fileName,
                                        String productModel,
                                        DocumentStatus status,
                                        Long compilerId,
                                        Pageable pageable) {
        return documentRepository.searchDocuments(
                fileNumber, fileName, productModel, status, compilerId, pageable
        );
    }
}

