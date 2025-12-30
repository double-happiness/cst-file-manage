package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.model.entity.User;
import com.zsx.cstfilemanage.domain.model.entity.UserGroup;
import com.zsx.cstfilemanage.domain.model.entity.UserGroupMember;
import com.zsx.cstfilemanage.domain.repository.UserGroupMemberRepository;
import com.zsx.cstfilemanage.domain.repository.UserGroupRepository;
import com.zsx.cstfilemanage.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户组管理服务
 */
@Service
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
        // 检查用户组代码是否已存在
        if (userGroupRepository.findByGroupCode(userGroup.getGroupCode()).isPresent()) {
            throw new BizException(ErrorCode.USERGROUP_CODE_EXISTS);
        }
        return userGroupRepository.save(userGroup);
    }

    /**
     * 更新用户组
     */
    @Transactional
    public UserGroup updateUserGroup(Long groupId, UserGroup userGroup) {
        UserGroup existing = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new BizException(ErrorCode.USERGROUP_NOT_FOUND));

        if (userGroup.getGroupName() != null) {
            existing.setGroupName(userGroup.getGroupName());
        }
        if (userGroup.getDescription() != null) {
            existing.setDescription(userGroup.getDescription());
        }
        if (userGroup.getEnabled() != null) {
            existing.setEnabled(userGroup.getEnabled());
        }

        return userGroupRepository.save(existing);
    }

    /**
     * 删除用户组
     */
    @Transactional
    public void deleteUserGroup(Long groupId) {
        UserGroup userGroup = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new BizException(ErrorCode.USERGROUP_NOT_FOUND));

        // 删除所有成员关联
        List<UserGroupMember> members = userGroupMemberRepository.findByGroupId(groupId);
        userGroupMemberRepository.deleteAll(members);

        userGroupRepository.delete(userGroup);
    }

    /**
     * 添加用户到用户组
     */
    @Transactional
    public void addMembersToGroup(Long groupId, List<Long> userIds) {
        UserGroup group = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new BizException(ErrorCode.USERGROUP_NOT_FOUND));

        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));

            // 检查是否已存在
            if (userGroupMemberRepository.findByGroupIdAndUserId(groupId, userId).isEmpty()) {
                UserGroupMember member = new UserGroupMember();
                member.setGroupId(groupId);
                member.setUserId(userId);
                userGroupMemberRepository.save(member);
            }
        }
    }

    /**
     * 从用户组移除用户
     */
    @Transactional
    public void removeMembersFromGroup(Long groupId, List<Long> userIds) {
        for (Long userId : userIds) {
            userGroupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                    .ifPresent(userGroupMemberRepository::delete);
        }
    }

    /**
     * 获取用户组的所有成员
     */
    public List<User> getGroupMembers(Long groupId) {
        List<Long> userIds = userGroupMemberRepository.findUserIdsByGroupId(groupId);
        return userIds.stream()
                .map(userId -> userRepository.findById(userId).orElse(null))
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户所属的所有用户组
     */
    public List<UserGroup> getUserGroups(Long userId) {
        List<Long> groupIds = userGroupMemberRepository.findGroupIdsByUserId(userId);
        return groupIds.stream()
                .map(groupId -> userGroupRepository.findById(groupId).orElse(null))
                .filter(group -> group != null)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有用户组
     */
    public List<UserGroup> getAllUserGroups() {
        return userGroupRepository.findAll();
    }

    /**
     * 根据ID查询用户组
     */
    public UserGroup getUserGroupById(Long id) {
        return userGroupRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.USERGROUP_NOT_FOUND));
    }
}

