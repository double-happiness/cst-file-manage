package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.PermissionService;
import com.zsx.cstfilemanage.application.service.RoleService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.model.entity.Permission;
import com.zsx.cstfilemanage.domain.model.entity.Role;
import com.zsx.cstfilemanage.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/v1/roles")
@Slf4j
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
        log.debug("=== 创建角色接口调用开始 ===");
        log.info("创建角色请求 - 角色代码: {}, 角色名称: {}", role.getRoleCode(), role.getRoleName());
        try {
            Role created = roleService.createRole(role);
            log.info("创建角色成功 - 角色ID: {}, 角色代码: {}, 角色名称: {}", 
                    created.getId(), created.getRoleCode(), created.getRoleName());
            log.debug("=== 创建角色接口调用结束 ===");
            return ApiResponse.success(created);
        } catch (Exception e) {
            log.error("创建角色失败 - 角色代码: {}, 错误信息: {}", role.getRoleCode(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @RequiresPermission("role:update")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        log.debug("=== 更新角色接口调用开始 ===");
        log.info("更新角色请求 - 角色ID: {}, 角色代码: {}, 角色名称: {}", id, role.getRoleCode(), role.getRoleName());
        try {
            Role updated = roleService.updateRole(id, role);
            log.info("更新角色成功 - 角色ID: {}, 角色代码: {}", updated.getId(), updated.getRoleCode());
            log.debug("=== 更新角色接口调用结束 ===");
            return ApiResponse.success(updated);
        } catch (Exception e) {
            log.error("更新角色失败 - 角色ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @RequiresPermission("role:delete")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        log.debug("=== 删除角色接口调用开始 ===");
        log.info("删除角色请求 - 角色ID: {}", id);
        try {
            roleService.deleteRole(id);
            log.info("删除角色成功 - 角色ID: {}", id);
            log.debug("=== 删除角色接口调用结束 ===");
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("删除角色失败 - 角色ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取所有角色
     */
    @GetMapping
    @RequiresPermission("role:view")
    public ApiResponse<List<Role>> getAllRoles() {
        log.debug("=== 获取所有角色接口调用开始 ===");
        try {
            List<Role> roles = roleService.getAllRoles();
            log.info("获取所有角色成功 - 角色数量: {}", roles.size());
            log.debug("=== 获取所有角色接口调用结束 ===");
            return ApiResponse.success(roles);
        } catch (Exception e) {
            log.error("获取所有角色失败 - 错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @RequiresPermission("role:view")
    public ApiResponse<Role> getRoleById(@PathVariable Long id) {
        log.debug("=== 获取角色详情接口调用开始 ===");
        log.info("获取角色详情请求 - 角色ID: {}", id);
        try {
            Role role = roleService.getRoleById(id);
            log.info("获取角色详情成功 - 角色ID: {}, 角色代码: {}, 角色名称: {}", 
                    role.getId(), role.getRoleCode(), role.getRoleName());
            log.debug("=== 获取角色详情接口调用结束 ===");
            return ApiResponse.success(role);
        } catch (Exception e) {
            log.error("获取角色详情失败 - 角色ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/{id}/permissions")
    @RequiresPermission("role:assign")
    public ApiResponse<Void> assignPermissions(@PathVariable Long id, @RequestBody AssignPermissionsRequest request) {
        log.debug("=== 为角色分配权限接口调用开始 ===");
        log.info("为角色分配权限请求 - 角色ID: {}, 权限ID列表: {}", id, request.getPermissionIds());
        try {
            permissionService.assignPermissionsToRole(id, request.getPermissionIds());
            log.info("为角色分配权限成功 - 角色ID: {}, 权限数量: {}", id, request.getPermissionIds().size());
            log.debug("=== 为角色分配权限接口调用结束 ===");
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("为角色分配权限失败 - 角色ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取角色的权限
     */
    @GetMapping("/{id}/permissions")
    @RequiresPermission("role:view")
    public ApiResponse<List<Permission>> getRolePermissions(@PathVariable Long id) {
        log.debug("=== 获取角色权限接口调用开始 ===");
        log.info("获取角色权限请求 - 角色ID: {}", id);
        try {
            List<Permission> permissions = permissionService.getRolePermissions(id);
            log.info("获取角色权限成功 - 角色ID: {}, 权限数量: {}", id, permissions.size());
            log.debug("=== 获取角色权限接口调用结束 ===");
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            log.error("获取角色权限失败 - 角色ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assign")
    @RequiresPermission("role:assign")
    public ApiResponse<Void> assignRolesToUser(@RequestBody AssignRolesRequest request) {
        log.debug("=== 为用户分配角色接口调用开始 ===");
        log.info("为用户分配角色请求 - 用户ID: {}, 角色ID列表: {}", request.getUserId(), request.getRoleIds());
        try {
            roleService.assignRolesToUser(request.getUserId(), request.getRoleIds());
            log.info("为用户分配角色成功 - 用户ID: {}, 角色数量: {}", request.getUserId(), request.getRoleIds().size());
            log.debug("=== 为用户分配角色接口调用结束 ===");
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("为用户分配角色失败 - 用户ID: {}, 错误信息: {}", request.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取用户的角色
     */
    @GetMapping("/user/{userId}")
    @RequiresPermission("role:view")
    public ApiResponse<List<Role>> getUserRoles(@PathVariable Long userId) {
        log.debug("=== 获取用户角色接口调用开始 ===");
        log.info("获取用户角色请求 - 用户ID: {}", userId);
        try {
            List<Role> roles = roleService.getUserRoles(userId);
            log.info("获取用户角色成功 - 用户ID: {}, 角色数量: {}", userId, roles.size());
            log.debug("=== 获取用户角色接口调用结束 ===");
            return ApiResponse.success(roles);
        } catch (Exception e) {
            log.error("获取用户角色失败 - 用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            throw e;
        }
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

