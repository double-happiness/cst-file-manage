package com.zsx.cstfilemanage.domain.storage;

public interface OcsClient {

    UploadToken generateUploadToken(String objectKey, int expireSeconds);
}
