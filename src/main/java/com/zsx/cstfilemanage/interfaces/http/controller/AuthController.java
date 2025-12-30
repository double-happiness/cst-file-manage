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
        log.debug("=== 用户登录接口调用开始 ===");
        log.info("用户登录请求 - 用户名: {}", request.getUsername());
        try {
            AuthService.LoginResponse response = authService.login(request.getUsername(), request.getPassword());
            log.info("用户登录成功 - 用户ID: {}, 用户名: {}, 真实姓名: {}", 
                    response.getUserId(), response.getUsername(), response.getRealName());
            log.debug("=== 用户登录接口调用结束 ===");
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("用户登录失败 - 用户名: {}, 错误信息: {}", request.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
}

