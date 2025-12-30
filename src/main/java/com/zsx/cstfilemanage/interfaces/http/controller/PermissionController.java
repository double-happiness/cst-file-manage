package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.PermissionService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.model.entity.Permission;
import com.zsx.cstfilemanage.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/v1/permissions")
@Slf4j
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 创建权限
     */
    @PostMapping
    @RequiresPermission("permission:create")
    public ApiResponse<Permission> createPermission(@Valid @RequestBody Permission permission) {
        log.debug("=== 创建权限接口调用开始 ===");
        log.info("创建权限请求 - 权限代码: {}, 权限名称: {}", permission.getPermissionCode(), permission.getPermissionName());
        try {
            Permission created = permissionService.createPermission(permission);
            log.info("创建权限成功 - 权限ID: {}, 权限代码: {}", created.getId(), created.getPermissionCode());
            log.debug("=== 创建权限接口调用结束 ===");
            return ApiResponse.success(created);
        } catch (Exception e) {
            log.error("创建权限失败 - 权限代码: {}, 错误信息: {}", permission.getPermissionCode(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @RequiresPermission("permission:update")
    public ApiResponse<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        log.debug("=== 更新权限接口调用开始 ===");
        log.info("更新权限请求 - 权限ID: {}, 权限代码: {}", id, permission.getPermissionCode());
        try {
            Permission updated = permissionService.updatePermission(id, permission);
            log.info("更新权限成功 - 权限ID: {}, 权限代码: {}", updated.getId(), updated.getPermissionCode());
            log.debug("=== 更新权限接口调用结束 ===");
            return ApiResponse.success(updated);
        } catch (Exception e) {
            log.error("更新权限失败 - 权限ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取所有权限
     */
    @GetMapping
    @RequiresPermission("permission:view")
    public ApiResponse<List<Permission>> getAllPermissions() {
        log.debug("=== 获取所有权限接口调用开始 ===");
        try {
            List<Permission> permissions = permissionService.getAllPermissions();
            log.info("获取所有权限成功 - 权限数量: {}", permissions.size());
            log.debug("=== 获取所有权限接口调用结束 ===");
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            log.error("获取所有权限失败 - 错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    @RequiresPermission("permission:view")
    public ApiResponse<List<Permission>> getPermissionTree() {
        log.debug("=== 获取权限树接口调用开始 ===");
        try {
            List<Permission> tree = permissionService.getPermissionTree();
            log.info("获取权限树成功 - 节点数量: {}", tree.size());
            log.debug("=== 获取权限树接口调用结束 ===");
            return ApiResponse.success(tree);
        } catch (Exception e) {
            log.error("获取权限树失败 - 错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取当前用户的权限
     */
    @GetMapping("/my")
    public ApiResponse<Set<String>> getMyPermissions() {
        log.debug("=== 获取当前用户权限接口调用开始 ===");
        Long userId = com.zsx.cstfilemanage.infrastructure.security.SecurityContext.getCurrentUserId();
        if (userId == null) {
            log.warn("获取当前用户权限 - 用户未登录");
            return ApiResponse.success(Set.of());
        }
        try {
            Set<String> permissions = permissionService.getUserPermissions(userId);
            log.info("获取当前用户权限成功 - 用户ID: {}, 权限数量: {}", userId, permissions.size());
            log.debug("=== 获取当前用户权限接口调用结束 ===");
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            log.error("获取当前用户权限失败 - 用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
}

