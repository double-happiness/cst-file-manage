package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.model.entity.User;
import com.zsx.cstfilemanage.domain.model.entity.UserGroup;
import com.zsx.cstfilemanage.domain.model.entity.UserGroupMember;
import com.zsx.cstfilemanage.domain.repository.UserGroupMemberRepository;
import com.zsx.cstfilemanage.domain.repository.UserGroupRepository;
import com.zsx.cstfilemanage.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户组管理服务
 */
@Service
@Slf4j
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;
    private final UserRepository userRepository;

    public UserGroupService(UserGroupRepository userGroupRepository,
                           UserGroupMemberRepository userGroupMemberRepository,
                           UserRepository userRepository) {
        this.userGroupRepository = userGroupRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
        this.userRepository = userRepository;
    }

    /**
     * 创建用户组
     */
    @Transactional
    public UserGroup createUserGroup(UserGroup userGroup) {
        log.debug("=== UserGroupService.createUserGroup 开始 ===");
        log.info("创建用户组 - 用户组代码: {}, 用户组名称: {}", userGroup.getGroupCode(), userGroup.getGroupName());
        
        // 检查用户组代码是否已存在
        if (userGroupRepository.findByGroupCode(userGroup.getGroupCode()).isPresent()) {
            log.warn("创建用户组失败 - 用户组代码已存在: {}", userGroup.getGroupCode());
            throw new BizException(ErrorCode.USERGROUP_CODE_EXISTS);
        }
        
        UserGroup saved = userGroupRepository.save(userGroup);
        log.info("创建用户组成功 - 用户组ID: {}, 用户组代码: {}", saved.getId(), saved.getGroupCode());
        log.debug("=== UserGroupService.createUserGroup 结束 ===");
        return saved;
    }

    /**
     * 更新用户组
     */
    @Transactional
    public UserGroup updateUserGroup(Long groupId, UserGroup userGroup) {
        log.debug("=== UserGroupService.updateUserGroup 开始 ===");
        log.info("更新用户组 - 用户组ID: {}, 用户组代码: {}", groupId, userGroup.getGroupCode());
        
        UserGroup existing = userGroupRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("更新用户组失败 - 用户组不存在: {}", groupId);
                    return new BizException(ErrorCode.USERGROUP_NOT_FOUND);
                });

        if (userGroup.getGroupName() != null) {
            existing.setGroupName(userGroup.getGroupName());
            log.debug("更新用户组 - 用户组名称: {}", userGroup.getGroupName());
        }
        if (userGroup.getDescription() != null) {
            existing.setDescription(userGroup.getDescription());
            log.debug("更新用户组 - 描述: {}", userGroup.getDescription());
        }
        if (userGroup.getEnabled() != null) {
            existing.setEnabled(userGroup.getEnabled());
            log.debug("更新用户组 - 启用状态: {}", userGroup.getEnabled());
        }

        UserGroup saved = userGroupRepository.save(existing);
        log.info("更新用户组成功 - 用户组ID: {}, 用户组代码: {}", saved.getId(), saved.getGroupCode());
        log.debug("=== UserGroupService.updateUserGroup 结束 ===");
        return saved;
    }

    /**
     * 删除用户组
     */
    @Transactional
    public void deleteUserGroup(Long groupId) {
        log.debug("=== UserGroupService.deleteUserGroup 开始 ===");
        log.info("删除用户组 - 用户组ID: {}", groupId);
        
        UserGroup userGroup = userGroupRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("删除用户组失败 - 用户组不存在: {}", groupId);
                    return new BizException(ErrorCode.USERGROUP_NOT_FOUND);
                });

        // 删除所有成员关联
        List<UserGroupMember> members = userGroupMemberRepository.findByGroupId(groupId);
        log.debug("删除用户组 - 删除成员关联数量: {}", members.size());
        userGroupMemberRepository.deleteAll(members);

        userGroupRepository.delete(userGroup);
        log.info("删除用户组成功 - 用户组ID: {}, 用户组代码: {}", groupId, userGroup.getGroupCode());
        log.debug("=== UserGroupService.deleteUserGroup 结束 ===");
    }

    /**
     * 添加用户到用户组（优化：使用批量查询）
     */
    @Transactional
    public void addMembersToGroup(Long groupId, List<Long> userIds) {
        log.debug("=== UserGroupService.addMembersToGroup 开始 ===");
        log.info("添加用户到用户组 - 用户组ID: {}, 用户数量: {}", groupId, userIds.size());
        
        UserGroup group = userGroupRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("添加用户到用户组失败 - 用户组不存在: {}", groupId);
                    return new BizException(ErrorCode.USERGROUP_NOT_FOUND);
                });

        // 使用批量查询优化性能
        List<User> users = userRepository.findByIds(userIds);
        if (users.size() != userIds.size()) {
            log.warn("添加用户到用户组 - 部分用户不存在，期望: {}, 实际: {}", userIds.size(), users.size());
        }

        int addedCount = 0;
        for (Long userId : userIds) {
            // 检查是否已存在
            if (userGroupMemberRepository.findByGroupIdAndUserId(groupId, userId).isEmpty()) {
                UserGroupMember member = new UserGroupMember();
                member.setGroupId(groupId);
                member.setUserId(userId);
                userGroupMemberRepository.save(member);
                addedCount++;
            }
        }
        
        log.info("添加用户到用户组成功 - 用户组ID: {}, 新增成员数: {}", groupId, addedCount);
        log.debug("=== UserGroupService.addMembersToGroup 结束 ===");
    }

    /**
     * 从用户组移除用户
     */
    @Transactional
    public void removeMembersFromGroup(Long groupId, List<Long> userIds) {
        log.debug("=== UserGroupService.removeMembersFromGroup 开始 ===");
        log.info("从用户组移除用户 - 用户组ID: {}, 用户数量: {}", groupId, userIds.size());
        
        int removedCount = 0;
        for (Long userId : userIds) {
            userGroupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                    .ifPresent(member -> {
                        userGroupMemberRepository.delete(member);
                    });
            removedCount++;
        }
        
        log.info("从用户组移除用户成功 - 用户组ID: {}, 移除成员数: {}", groupId, removedCount);
        log.debug("=== UserGroupService.removeMembersFromGroup 结束 ===");
    }

    /**
     * 获取用户组的所有成员（优化：使用批量查询避免N+1问题）
     */
    public List<User> getGroupMembers(Long groupId) {
        log.debug("=== UserGroupService.getGroupMembers 开始 ===");
        log.info("获取用户组成员 - 用户组ID: {}", groupId);
        
        List<Long> userIds = userGroupMemberRepository.findUserIdsByGroupId(groupId);
        if (userIds.isEmpty()) {
            log.info("获取用户组成员 - 用户组ID: {}, 无成员", groupId);
            log.debug("=== UserGroupService.getGroupMembers 结束 ===");
            return List.of();
        }
        
        // 使用批量查询优化性能，避免N+1问题
        List<User> members = userRepository.findByIds(userIds);
        
        log.info("获取用户组成员成功 - 用户组ID: {}, 成员数量: {}", groupId, members.size());
        log.debug("=== UserGroupService.getGroupMembers 结束 ===");
        return members;
    }

    /**
     * 获取用户所属的所有用户组（优化：使用批量查询避免N+1问题）
     */
    public List<UserGroup> getUserGroups(Long userId) {
        log.debug("=== UserGroupService.getUserGroups 开始 ===");
        log.info("获取用户所属用户组 - 用户ID: {}", userId);
        
        List<Long> groupIds = userGroupMemberRepository.findGroupIdsByUserId(userId);
        if (groupIds.isEmpty()) {
            log.info("获取用户所属用户组 - 用户ID: {}, 无用户组", userId);
            log.debug("=== UserGroupService.getUserGroups 结束 ===");
            return List.of();
        }
        
        // 使用批量查询优化性能，避免N+1问题
        List<UserGroup> groups = userGroupRepository.findByIds(groupIds);
        
        log.info("获取用户所属用户组成功 - 用户ID: {}, 用户组数量: {}", userId, groups.size());
        log.debug("=== UserGroupService.getUserGroups 结束 ===");
        return groups;
    }

    /**
     * 获取所有用户组
     */
    public List<UserGroup> getAllUserGroups() {
        log.debug("=== UserGroupService.getAllUserGroups 开始 ===");
        List<UserGroup> groups = userGroupRepository.findAll();
        log.info("获取所有用户组成功 - 用户组数量: {}", groups.size());
        log.debug("=== UserGroupService.getAllUserGroups 结束 ===");
        return groups;
    }

    /**
     * 根据ID查询用户组
     */
    public UserGroup getUserGroupById(Long id) {
        log.debug("=== UserGroupService.getUserGroupById 开始 ===");
        log.info("查询用户组 - 用户组ID: {}", id);
        
        UserGroup group = userGroupRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("查询用户组失败 - 用户组不存在: {}", id);
                    return new BizException(ErrorCode.USERGROUP_NOT_FOUND);
                });
        
        log.info("查询用户组成功 - 用户组ID: {}, 用户组代码: {}, 用户组名称: {}", 
                group.getId(), group.getGroupCode(), group.getGroupName());
        log.debug("=== UserGroupService.getUserGroupById 结束 ===");
        return group;
    }
}

