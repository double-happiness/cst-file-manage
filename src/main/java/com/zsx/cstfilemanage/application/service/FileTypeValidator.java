package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.domain.cenum.FileType;

/**
 * 文件类型验证器接口
 */
public interface FileTypeValidator {
    boolean isAllowed(FileType fileType, String extension);
}

