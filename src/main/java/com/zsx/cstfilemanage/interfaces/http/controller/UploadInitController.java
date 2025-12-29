package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.UploadInitAppService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.interfaces.http.request.UploadInitRequest;
import com.zsx.cstfilemanage.interfaces.http.response.UploadInitResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/files/upload")
public class UploadInitController {

    private final UploadInitAppService uploadInitAppService;

    public UploadInitController(UploadInitAppService uploadInitAppService) {
        this.uploadInitAppService = uploadInitAppService;
    }

    @PostMapping("/init")
    public ApiResponse<UploadInitResponse> init(@Valid @RequestBody UploadInitRequest request) {
        UploadInitResponse response = uploadInitAppService.initUpload(request);
        return ApiResponse.success(response);
    }
}
