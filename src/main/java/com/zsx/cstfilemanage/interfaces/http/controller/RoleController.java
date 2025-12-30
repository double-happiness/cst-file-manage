package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.PermissionService;
import com.zsx.cstfilemanage.application.service.RoleService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.model.entity.Permission;
import com.zsx.cstfilemanage.domain.model.entity.Role;
import com.zsx.cstfilemanage.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    public RoleController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    /**
     * 创建角色
     */
    @PostMapping
    @RequiresPermission("role:create")
    public ApiResponse<Role> createRole(@Valid @RequestBody Role role) {
        Role created = roleService.createRole(role);
        return ApiResponse.success(created);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @RequiresPermission("role:update")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        Role updated = roleService.updateRole(id, role);
        return ApiResponse.success(updated);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @RequiresPermission("role:delete")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success(null);
    }

    /**
     * 获取所有角色
     */
    @GetMapping
    @RequiresPermission("role:view")
    public ApiResponse<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ApiResponse.success(roles);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @RequiresPermission("role:view")
    public ApiResponse<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ApiResponse.success(role);
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/{id}/permissions")
    @RequiresPermission("role:assign")
    public ApiResponse<Void> assignPermissions(@PathVariable Long id, @RequestBody AssignPermissionsRequest request) {
        permissionService.assignPermissionsToRole(id, request.getPermissionIds());
        return ApiResponse.success(null);
    }

    /**
     * 获取角色的权限
     */
    @GetMapping("/{id}/permissions")
    @RequiresPermission("role:view")
    public ApiResponse<List<Permission>> getRolePermissions(@PathVariable Long id) {
        List<Permission> permissions = permissionService.getRolePermissions(id);
        return ApiResponse.success(permissions);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assign")
    @RequiresPermission("role:assign")
    public ApiResponse<Void> assignRolesToUser(@RequestBody AssignRolesRequest request) {
        roleService.assignRolesToUser(request.getUserId(), request.getRoleIds());
        return ApiResponse.success(null);
    }

    /**
     * 获取用户的角色
     */
    @GetMapping("/user/{userId}")
    @RequiresPermission("role:view")
    public ApiResponse<List<Role>> getUserRoles(@PathVariable Long userId) {
        List<Role> roles = roleService.getUserRoles(userId);
        return ApiResponse.success(roles);
    }

    /**
     * 分配权限请求
     */
    public static class AssignPermissionsRequest {
        private List<Long> permissionIds;

        public List<Long> getPermissionIds() {
            return permissionIds;
        }

        public void setPermissionIds(List<Long> permissionIds) {
            this.permissionIds = permissionIds;
        }
    }

    /**
     * 分配角色请求
     */
    public static class AssignRolesRequest {
        private Long userId;
        private List<Long> roleIds;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public List<Long> getRoleIds() {
            return roleIds;
        }

        public void setRoleIds(List<Long> roleIds) {
            this.roleIds = roleIds;
        }
    }
}

