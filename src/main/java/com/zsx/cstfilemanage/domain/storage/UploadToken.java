package com.zsx.cstfilemanage.domain.storage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadToken {

    private String uploadUrl;
    private Long expireAt;
}
