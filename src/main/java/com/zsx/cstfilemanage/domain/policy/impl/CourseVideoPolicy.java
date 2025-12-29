package com.zsx.cstfilemanage.domain.policy.impl;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.policy.FileTypePolicy;
import org.springframework.stereotype.Component;

@Component
public class CourseVideoPolicy implements FileTypePolicy {

    @Override
    public String bizType() {
        return "course_video";
    }

    @Override
    public void validate(String contentType, long fileSize) {
        if (!"video/mp4".equals(contentType)) {
            throw new BizException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
        if (fileSize > 500 * 1024 * 1024) {
            throw new BizException(ErrorCode.FILE_TOO_LARGE);
        }
    }
}
