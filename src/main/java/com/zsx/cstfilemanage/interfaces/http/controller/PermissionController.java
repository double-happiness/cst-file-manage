package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.PermissionService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.model.entity.Permission;
import com.zsx.cstfilemanage.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/v1/permissions")
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
        Permission created = permissionService.createPermission(permission);
        return ApiResponse.success(created);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @RequiresPermission("permission:update")
    public ApiResponse<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        Permission updated = permissionService.updatePermission(id, permission);
        return ApiResponse.success(updated);
    }

    /**
     * 获取所有权限
     */
    @GetMapping
    @RequiresPermission("permission:view")
    public ApiResponse<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ApiResponse.success(permissions);
    }

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    @RequiresPermission("permission:view")
    public ApiResponse<List<Permission>> getPermissionTree() {
        List<Permission> tree = permissionService.getPermissionTree();
        return ApiResponse.success(tree);
    }

    /**
     * 获取当前用户的权限
     */
    @GetMapping("/my")
    public ApiResponse<Set<String>> getMyPermissions() {
        Long userId = com.zsx.cstfilemanage.infrastructure.security.SecurityContext.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.success(Set.of());
        }
        Set<String> permissions = permissionService.getUserPermissions(userId);
        return ApiResponse.success(permissions);
    }
}

