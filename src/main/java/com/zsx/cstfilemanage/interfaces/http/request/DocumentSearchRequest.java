package com.zsx.cstfilemanage.interfaces.http.request;

import com.zsx.cstfilemanage.domain.cenum.DocumentStatus;
import lombok.Data;

@Data
public class DocumentSearchRequest {

    private String fileNumber;
    private String fileName;
    private String productModel;
    private DocumentStatus status;
    private Long compilerId;

    // 分页参数
    private Integer page = 0;
    private Integer size = 20;
}
