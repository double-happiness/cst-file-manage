package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.AuthService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.interfaces.http.request.LoginRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public ApiResponse<AuthService.LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthService.LoginResponse response = authService.login(request.getUsername(), request.getPassword());
        return ApiResponse.success(response);
    }
}

