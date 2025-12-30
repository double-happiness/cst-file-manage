package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.model.entity.Permission;
import com.zsx.cstfilemanage.domain.model.entity.Role;
import com.zsx.cstfilemanage.domain.model.entity.RolePermission;
import com.zsx.cstfilemanage.domain.repository.PermissionRepository;
import com.zsx.cstfilemanage.domain.repository.RolePermissionRepository;
import com.zsx.cstfilemanage.domain.repository.RoleRepository;
import com.zsx.cstfilemanage.domain.repository.UserRoleRepository;
import com.zsx.cstfilemanage.infrastructure.security.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限管理服务
 */
@Service
@Slf4j
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    public PermissionService(PermissionRepository permissionRepository,
                            RoleRepository roleRepository,
                            RolePermissionRepository rolePermissionRepository,
                            UserRoleRepository userRoleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * 检查用户是否拥有指定权限
     */
    public boolean hasPermission(Long userId, String permissionCode) {
        // 获取用户的所有角色
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return false;
        }

        // 查找权限
        Permission permission = permissionRepository.findByPermissionCode(permissionCode);
        if (permission == null) {
            return false;
        }

        // 检查用户角色是否拥有该权限
        for (Long roleId : roleIds) {
            if (rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permission.getId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查当前用户是否拥有指定权限
     */
    public boolean hasPermission(String permissionCode) {
        Long userId = SecurityContext.getCurrentUserId();
        if (userId == null) {
            return false;
        }
        return hasPermission(userId, permissionCode);
    }

    /**
     * 获取用户的所有权限代码
     */
    public Set<String> getUserPermissions(Long userId) {
        // 获取用户的所有角色
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return Set.of();
        }

        // 获取所有角色的权限ID
        Set<Long> permissionIds = roleIds.stream()
                .flatMap(roleId -> rolePermissionRepository.findPermissionIdsByRoleId(roleId).stream())
                .collect(Collectors.toSet());

        // 获取权限代码
        return permissionIds.stream()
                .map(permissionId -> permissionRepository.findById(permissionId).orElse(null))
                .filter(permission -> permission != null)
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }

    /**
     * 为角色分配权限
     */
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BizException(new ErrorCode(1024, "角色不存在")));

        // 删除原有权限
        rolePermissionRepository.deleteByRoleId(roleId);

        // 分配新权限
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new BizException(new ErrorCode(1025, "权限不存在")));

            // 检查是否已存在
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionRepository.save(rolePermission);
            }
        }
    }

    /**
     * 获取角色的所有权限
     */
    public List<Permission> getRolePermissions(Long roleId) {
        List<Long> permissionIds = rolePermissionRepository.findPermissionIdsByRoleId(roleId);
        return permissionIds.stream()
                .map(permissionId -> permissionRepository.findById(permissionId).orElse(null))
                .filter(permission -> permission != null)
                .collect(Collectors.toList());
    }

    /**
     * 创建权限
     */
    @Transactional
    public Permission createPermission(Permission permission) {
        // 检查权限代码是否已存在
        if (permissionRepository.findByPermissionCode(permission.getPermissionCode()) != null) {
            throw new BizException(new ErrorCode(1026, "权限代码已存在"));
        }
        return permissionRepository.save(permission);
    }

    /**
     * 更新权限
     */
    @Transactional
    public Permission updatePermission(Long permissionId, Permission permission) {
        Permission existing = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BizException(new ErrorCode(1025, "权限不存在")));

        if (permission.getPermissionName() != null) {
            existing.setPermissionName(permission.getPermissionName());
        }
        if (permission.getDescription() != null) {
            existing.setDescription(permission.getDescription());
        }
        if (permission.getPermissionType() != null) {
            existing.setPermissionType(permission.getPermissionType());
        }
        if (permission.getParentId() != null) {
            existing.setParentId(permission.getParentId());
        }

        return permissionRepository.save(existing);
    }

    /**
     * 获取所有权限
     */
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    /**
     * 获取权限树
     */
    public List<Permission> getPermissionTree() {
        List<Permission> allPermissions = permissionRepository.findAll();
        // 构建权限树（简化实现，实际可以递归构建）
        return allPermissions;
    }
}

