package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.UploadSession;

public interface UploadSessionRepository {

    void save(UploadSession session);

    UploadSession get(String uploadId);
}
