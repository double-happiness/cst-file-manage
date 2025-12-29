package com.zsx.cstfilemanage.domain.policy.impl;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.policy.FileTypeValidator;
import org.springframework.stereotype.Component;

@Component
public class ImagePolicy implements FileTypeValidator {

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_TYPES = {
            "image/jpeg", "image/png", "image/gif", "image/webp"
    };

    @Override
    public void validate(String contentType, long fileSize) {
        boolean allowed = false;
        for (String type : ALLOWED_TYPES) {
            if (type.equalsIgnoreCase(contentType)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new BizException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
        if (fileSize > MAX_SIZE) {
            throw new BizException(ErrorCode.FILE_TOO_LARGE);
        }
    }
}
