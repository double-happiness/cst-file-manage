package com.zsx.cstfilemanage.domain.policy;

public interface FileTypePolicy {

    String bizType();

    void validate(String contentType, long fileSize);
}
