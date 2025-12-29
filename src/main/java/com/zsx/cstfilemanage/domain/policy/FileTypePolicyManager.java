package com.zsx.cstfilemanage.domain.policy;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FileTypePolicyManager {

    private final Map<String, FileTypePolicy> policyMap = new HashMap<>();

    public FileTypePolicyManager(List<FileTypePolicy> policies) {
        for (FileTypePolicy policy : policies) {
            policyMap.put(policy.bizType(), policy);
        }
    }

    public void validate(String bizType, String contentType, long fileSize) {
        FileTypePolicy policy = policyMap.get(bizType);
        if (policy == null) {
            throw new BizException(ErrorCode.BIZ_TYPE_NOT_SUPPORTED);
        }
        policy.validate(contentType, fileSize);
    }
}
