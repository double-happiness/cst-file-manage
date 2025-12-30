package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.UserService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ApiResponse<User> createUser(@RequestBody User user) {
        log.debug("=== 创建用户接口调用开始 ===");
        log.info("创建用户请求 - 用户名: {}, 真实姓名: {}, 部门: {}, 邮箱: {}", 
                user.getUsername(), user.getRealName(), user.getDepartmentName(), user.getEmail());
        try {
            User created = userService.createUser(user);
            log.info("创建用户成功 - 用户ID: {}, 用户名: {}", created.getId(), created.getUsername());
            log.debug("=== 创建用户接口调用结束 ===");
            return ApiResponse.success(created);
        } catch (Exception e) {
            log.error("创建用户失败 - 用户名: {}, 错误信息: {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 创建用户（兼容旧接口）
     */
    @PostMapping("/singnUp")
    public ApiResponse<User> createUserLegacy(@RequestBody User user) {
        log.debug("=== 创建用户接口调用开始（兼容旧接口） ===");
        log.info("创建用户请求（旧接口） - 用户名: {}, 真实姓名: {}", user.getUsername(), user.getRealName());
        try {
            User created = userService.createUser(user);
            log.info("创建用户成功（旧接口） - 用户ID: {}, 用户名: {}", created.getId(), created.getUsername());
            log.debug("=== 创建用户接口调用结束（兼容旧接口） ===");
            return ApiResponse.success(created);
        } catch (Exception e) {
            log.error("创建用户失败（旧接口） - 用户名: {}, 错误信息: {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.debug("=== 更新用户接口调用开始 ===");
        log.info("更新用户请求 - 用户ID: {}, 用户名: {}, 真实姓名: {}", id, user.getUsername(), user.getRealName());
        try {
            User updated = userService.updateUser(id, user);
            log.info("更新用户成功 - 用户ID: {}, 用户名: {}", updated.getId(), updated.getUsername());
            log.debug("=== 更新用户接口调用结束 ===");
            return ApiResponse.success(updated);
        } catch (Exception e) {
            log.error("更新用户失败 - 用户ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/{id}/reset-password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest request) {
        log.debug("=== 重置密码接口调用开始 ===");
        log.info("重置密码请求 - 用户ID: {}", id);
        try {
            userService.resetPassword(id, request.getNewPassword());
            log.info("重置密码成功 - 用户ID: {}", id);
            log.debug("=== 重置密码接口调用结束 ===");
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("重置密码失败 - 用户ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 查询所有用户
     */
    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        log.debug("=== 查询所有用户接口调用开始 ===");
        try {
            List<User> users = userService.getAllUsers();
            log.info("查询所有用户成功 - 用户数量: {}", users.size());
            log.debug("=== 查询所有用户接口调用结束 ===");
            return ApiResponse.success(users);
        } catch (Exception e) {
            log.error("查询所有用户失败 - 错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        log.debug("=== 查询用户详情接口调用开始 ===");
        log.info("查询用户详情请求 - 用户ID: {}", id);
        try {
            User user = userService.getUserById(id);
            log.info("查询用户详情成功 - 用户ID: {}, 用户名: {}, 真实姓名: {}", 
                    user.getId(), user.getUsername(), user.getRealName());
            log.debug("=== 查询用户详情接口调用结束 ===");
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("查询用户详情失败 - 用户ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 重置密码请求
     */
    public static class ResetPasswordRequest {
        private String newPassword;

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}

