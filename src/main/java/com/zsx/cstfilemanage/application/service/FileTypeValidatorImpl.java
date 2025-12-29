package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.cenum.FileType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 文件类型验证器实现
 */
@Component
public class FileTypeValidatorImpl implements FileTypeValidator {

    // 允许的文件类型
    private static final List<FileType> ALLOWED_TYPES = Arrays.asList(
            FileType.CAD_DWG,
            FileType.CAD_DXF,
            FileType.PDF,
            FileType.JPEG,
            FileType.PNG,
            FileType.WORD,
            FileType.EXCEL
    );

    @Override
    public boolean isAllowed(FileType fileType, String extension) {
        if (fileType == null || fileType == FileType.OTHER) {
            return false;
        }
        return ALLOWED_TYPES.contains(fileType);
    }
}

