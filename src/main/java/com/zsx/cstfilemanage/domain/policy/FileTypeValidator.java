package com.zsx.cstfilemanage.domain.policy;

/**
 * 文件类型校验器接口
 */

/**
 * 文件类型校验器接口
 */
public interface FileTypeValidator {

    /**
     * 校验文件类型是否合法
     *
     * @param contentType 文件 MIME 类型
     * @param fileSize    文件大小
     */
    void validate(String contentType, long fileSize);
}
