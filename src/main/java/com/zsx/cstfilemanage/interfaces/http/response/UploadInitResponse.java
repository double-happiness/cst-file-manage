package com.zsx.cstfilemanage.interfaces.http.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadInitResponse {

    private String uploadId;
    private String uploadUrl;
    private String objectKey;
    private Long expireAt;
    private String uploadMode;
}
