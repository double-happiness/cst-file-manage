package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.UserGroupService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.model.entity.User;
import com.zsx.cstfilemanage.domain.model.entity.UserGroup;
import com.zsx.cstfilemanage.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户组管理控制器
 */
@RestController
@RequestMapping("/api/v1/user-groups")
public class UserGroupController {

    private final UserGroupService userGroupService;

    public UserGroupController(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    /**
     * 创建用户组
     */
    @PostMapping
    @RequiresPermission("usergroup:create")
    public ApiResponse<UserGroup> createUserGroup(@Valid @RequestBody UserGroup userGroup) {
        UserGroup created = userGroupService.createUserGroup(userGroup);
        return ApiResponse.success(created);
    }

    /**
     * 更新用户组
     */
    @PutMapping("/{id}")
    @RequiresPermission("usergroup:update")
    public ApiResponse<UserGroup> updateUserGroup(@PathVariable Long id, @RequestBody UserGroup userGroup) {
        UserGroup updated = userGroupService.updateUserGroup(id, userGroup);
        return ApiResponse.success(updated);
    }

    /**
     * 删除用户组
     */
    @DeleteMapping("/{id}")
    @RequiresPermission("usergroup:delete")
    public ApiResponse<Void> deleteUserGroup(@PathVariable Long id) {
        userGroupService.deleteUserGroup(id);
        return ApiResponse.success(null);
    }

    /**
     * 获取所有用户组
     */
    @GetMapping
    @RequiresPermission("usergroup:view")
    public ApiResponse<List<UserGroup>> getAllUserGroups() {
        List<UserGroup> groups = userGroupService.getAllUserGroups();
        return ApiResponse.success(groups);
    }

    /**
     * 获取用户组详情
     */
    @GetMapping("/{id}")
    @RequiresPermission("usergroup:view")
    public ApiResponse<UserGroup> getUserGroupById(@PathVariable Long id) {
        UserGroup group = userGroupService.getUserGroupById(id);
        return ApiResponse.success(group);
    }

    /**
     * 添加用户到用户组
     */
    @PostMapping("/{id}/members")
    @RequiresPermission("usergroup:manage")
    public ApiResponse<Void> addMembers(@PathVariable Long id, @RequestBody AddMembersRequest request) {
        userGroupService.addMembersToGroup(id, request.getUserIds());
        return ApiResponse.success(null);
    }

    /**
     * 从用户组移除用户
     */
    @DeleteMapping("/{id}/members")
    @RequiresPermission("usergroup:manage")
    public ApiResponse<Void> removeMembers(@PathVariable Long id, @RequestBody RemoveMembersRequest request) {
        userGroupService.removeMembersFromGroup(id, request.getUserIds());
        return ApiResponse.success(null);
    }

    /**
     * 获取用户组的所有成员
     */
    @GetMapping("/{id}/members")
    @RequiresPermission("usergroup:view")
    public ApiResponse<List<User>> getGroupMembers(@PathVariable Long id) {
        List<User> members = userGroupService.getGroupMembers(id);
        return ApiResponse.success(members);
    }

    /**
     * 获取用户所属的所有用户组
     */
    @GetMapping("/user/{userId}")
    @RequiresPermission("usergroup:view")
    public ApiResponse<List<UserGroup>> getUserGroups(@PathVariable Long userId) {
        List<UserGroup> groups = userGroupService.getUserGroups(userId);
        return ApiResponse.success(groups);
    }

    /**
     * 添加成员请求
     */
    public static class AddMembersRequest {
        private List<Long> userIds;

        public List<Long> getUserIds() {
            return userIds;
        }

        public void setUserIds(List<Long> userIds) {
            this.userIds = userIds;
        }
    }

    /**
     * 移除成员请求
     */
    public static class RemoveMembersRequest {
        private List<Long> userIds;

        public List<Long> getUserIds() {
            return userIds;
        }

        public void setUserIds(List<Long> userIds) {
            this.userIds = userIds;
        }
    }
}

