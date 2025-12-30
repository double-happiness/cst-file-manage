package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.model.entity.Role;
import com.zsx.cstfilemanage.domain.model.entity.UserRole;
import com.zsx.cstfilemanage.domain.repository.RoleRepository;
import com.zsx.cstfilemanage.domain.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色管理服务
 */
@Service
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleService(RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * 创建角色
     */
    @Transactional
    public Role createRole(Role role) {
        log.debug("=== RoleService.createRole 开始 ===");
        log.info("创建角色 - 角色代码: {}, 角色名称: {}", role.getRoleCode(), role.getRoleName());
        
        // 检查角色代码是否已存在
        if (roleRepository.findByRoleCode(role.getRoleCode()).isPresent()) {
            log.warn("创建角色失败 - 角色代码已存在: {}", role.getRoleCode());
            throw new BizException(ErrorCode.ROLE_CODE_EXISTS);
        }
        
        Role saved = roleRepository.save(role);
        log.info("创建角色成功 - 角色ID: {}, 角色代码: {}, 角色名称: {}", 
                saved.getId(), saved.getRoleCode(), saved.getRoleName());
        log.debug("=== RoleService.createRole 结束 ===");
        return saved;
    }

    /**
     * 更新角色
     */
    @Transactional
    public Role updateRole(Long roleId, Role role) {
        log.debug("=== RoleService.updateRole 开始 ===");
        log.info("更新角色 - 角色ID: {}, 角色代码: {}", roleId, role.getRoleCode());
        
        Role existing = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    log.error("更新角色失败 - 角色不存在: {}", roleId);
                    return new BizException(ErrorCode.ROLE_NOT_FOUND);
                });

        if (role.getRoleName() != null) {
            existing.setRoleName(role.getRoleName());
            log.debug("更新角色 - 角色名称: {}", role.getRoleName());
        }
        if (role.getDescription() != null) {
            existing.setDescription(role.getDescription());
            log.debug("更新角色 - 描述: {}", role.getDescription());
        }
        if (role.getEnabled() != null) {
            existing.setEnabled(role.getEnabled());
            log.debug("更新角色 - 启用状态: {}", role.getEnabled());
        }

        Role saved = roleRepository.save(existing);
        log.info("更新角色成功 - 角色ID: {}, 角色代码: {}", saved.getId(), saved.getRoleCode());
        log.debug("=== RoleService.updateRole 结束 ===");
        return saved;
    }

    /**
     * 删除角色
     */
    @Transactional
    public void deleteRole(Long roleId) {
        log.debug("=== RoleService.deleteRole 开始 ===");
        log.info("删除角色 - 角色ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    log.error("删除角色失败 - 角色不存在: {}", roleId);
                    return new BizException(ErrorCode.ROLE_NOT_FOUND);
                });

        // 检查是否有用户使用该角色
        List<UserRole> userRoles = userRoleRepository.findByRoleId(roleId);
        if (!userRoles.isEmpty()) {
            log.warn("删除角色失败 - 角色正在使用中: {}, 使用用户数: {}", roleId, userRoles.size());
            throw new BizException(ErrorCode.ROLE_IN_USE);
        }

        roleRepository.delete(role);
        log.info("删除角色成功 - 角色ID: {}, 角色代码: {}", roleId, role.getRoleCode());
        log.debug("=== RoleService.deleteRole 结束 ===");
    }

    /**
     * 为用户分配角色
     */
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        log.debug("=== RoleService.assignRolesToUser 开始 ===");
        log.info("为用户分配角色 - 用户ID: {}, 角色ID列表: {}", userId, roleIds);
        
        // 删除用户原有角色
        List<UserRole> existingRoles = userRoleRepository.findByUserId(userId);
        log.debug("为用户分配角色 - 删除原有角色数量: {}", existingRoles.size());
        userRoleRepository.deleteAll(existingRoles);

        // 分配新角色
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> {
                        log.error("为用户分配角色失败 - 角色不存在: {}", roleId);
                        return new BizException(ErrorCode.ROLE_NOT_FOUND);
                    });

            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleRepository.save(userRole);
            log.debug("为用户分配角色 - 已分配角色: {} ({})", roleId, role.getRoleCode());
        }
        
        log.info("为用户分配角色成功 - 用户ID: {}, 角色数量: {}", userId, roleIds.size());
        log.debug("=== RoleService.assignRolesToUser 结束 ===");
    }

    /**
     * 获取用户的所有角色（优化：使用批量查询避免N+1问题）
     */
    public List<Role> getUserRoles(Long userId) {
        log.debug("=== RoleService.getUserRoles 开始 ===");
        log.info("获取用户角色 - 用户ID: {}", userId);
        
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            log.info("获取用户角色 - 用户ID: {}, 无角色", userId);
            log.debug("=== RoleService.getUserRoles 结束 ===");
            return List.of();
        }
        
        // 使用批量查询优化性能，避免N+1问题
        List<Role> roles = roleRepository.findByIds(roleIds);
        
        log.info("获取用户角色成功 - 用户ID: {}, 角色数量: {}", userId, roles.size());
        log.debug("=== RoleService.getUserRoles 结束 ===");
        return roles;
    }

    /**
     * 获取所有角色
     */
    public List<Role> getAllRoles() {
        log.debug("=== RoleService.getAllRoles 开始 ===");
        List<Role> roles = roleRepository.findAll();
        log.info("获取所有角色成功 - 角色数量: {}", roles.size());
        log.debug("=== RoleService.getAllRoles 结束 ===");
        return roles;
    }

    /**
     * 根据ID查询角色
     */
    public Role getRoleById(Long id) {
        log.debug("=== RoleService.getRoleById 开始 ===");
        log.info("查询角色 - 角色ID: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("查询角色失败 - 角色不存在: {}", id);
                    return new BizException(ErrorCode.ROLE_NOT_FOUND);
                });
        
        log.info("查询角色成功 - 角色ID: {}, 角色代码: {}, 角色名称: {}", 
                role.getId(), role.getRoleCode(), role.getRoleName());
        log.debug("=== RoleService.getRoleById 结束 ===");
        return role;
    }
}

