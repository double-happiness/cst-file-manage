package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.model.entity.Role;
import com.zsx.cstfilemanage.domain.model.entity.UserRole;
import com.zsx.cstfilemanage.domain.repository.RoleRepository;
import com.zsx.cstfilemanage.domain.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色管理服务
 */
@Service
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
        // 检查角色代码是否已存在
        if (roleRepository.findByRoleCode(role.getRoleCode()).isPresent()) {
            throw new BizException(ErrorCode.ROLE_CODE_EXISTS);
        }
        return roleRepository.save(role);
    }

    /**
     * 更新角色
     */
    @Transactional
    public Role updateRole(Long roleId, Role role) {
        Role existing = roleRepository.findById(roleId)
                .orElseThrow(() -> new BizException(ErrorCode.ROLE_NOT_FOUND));

        if (role.getRoleName() != null) {
            existing.setRoleName(role.getRoleName());
        }
        if (role.getDescription() != null) {
            existing.setDescription(role.getDescription());
        }
        if (role.getEnabled() != null) {
            existing.setEnabled(role.getEnabled());
        }

        return roleRepository.save(existing);
    }

    /**
     * 删除角色
     */
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BizException(ErrorCode.ROLE_NOT_FOUND));

        // 检查是否有用户使用该角色
        List<UserRole> userRoles = userRoleRepository.findByRoleId(roleId);
        if (!userRoles.isEmpty()) {
            throw new BizException(ErrorCode.ROLE_IN_USE);
        }

        roleRepository.delete(role);
    }

    /**
     * 为用户分配角色
     */
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // 删除用户原有角色
        List<UserRole> existingRoles = userRoleRepository.findByUserId(userId);
        userRoleRepository.deleteAll(existingRoles);

        // 分配新角色
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new BizException(ErrorCode.ROLE_NOT_FOUND));

            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleRepository.save(userRole);
        }
    }

    /**
     * 获取用户的所有角色
     */
    public List<Role> getUserRoles(Long userId) {
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        return roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId).orElse(null))
                .filter(role -> role != null)
                .toList();
    }

    /**
     * 获取所有角色
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * 根据ID查询角色
     */
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.ROLE_NOT_FOUND));
    }
}

