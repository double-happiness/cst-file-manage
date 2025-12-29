package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.interfaces.http.request.UploadInitRequest;
import com.zsx.cstfilemanage.interfaces.http.response.UploadInitResponse;
import com.zsx.cstfilemanage.application.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;

    // 单文件上传
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        return ResponseEntity.ok("文件上传成功: " + fileName);
    }
}
