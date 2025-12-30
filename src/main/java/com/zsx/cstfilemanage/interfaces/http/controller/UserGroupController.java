package com.zsx.cstfilemanage.interfaces.http.controller;

import com.zsx.cstfilemanage.application.service.UserGroupService;
import com.zsx.cstfilemanage.common.response.ApiResponse;
import com.zsx.cstfilemanage.domain.model.entity.User;
import com.zsx.cstfilemanage.domain.model.entity.UserGroup;
import com.zsx.cstfilemanage.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户组管理控制器
 */
@RestController
@RequestMapping("/api/v1/user-groups")
@Slf4j
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
        log.debug("=== 创建用户组接口调用开始 ===");
        log.info("创建用户组请求 - 用户组代码: {}, 用户组名称: {}", userGroup.getGroupCode(), userGroup.getGroupName());
        try {
            UserGroup created = userGroupService.createUserGroup(userGroup);
            log.info("创建用户组成功 - 用户组ID: {}, 用户组代码: {}", created.getId(), created.getGroupCode());
            log.debug("=== 创建用户组接口调用结束 ===");
            return ApiResponse.success(created);
        } catch (Exception e) {
            log.error("创建用户组失败 - 用户组代码: {}, 错误信息: {}", userGroup.getGroupCode(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新用户组
     */
    @PutMapping("/{id}")
    @RequiresPermission("usergroup:update")
    public ApiResponse<UserGroup> updateUserGroup(@PathVariable Long id, @RequestBody UserGroup userGroup) {
        log.debug("=== 更新用户组接口调用开始 ===");
        log.info("更新用户组请求 - 用户组ID: {}, 用户组代码: {}", id, userGroup.getGroupCode());
        try {
            UserGroup updated = userGroupService.updateUserGroup(id, userGroup);
            log.info("更新用户组成功 - 用户组ID: {}, 用户组代码: {}", updated.getId(), updated.getGroupCode());
            log.debug("=== 更新用户组接口调用结束 ===");
            return ApiResponse.success(updated);
        } catch (Exception e) {
            log.error("更新用户组失败 - 用户组ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除用户组
     */
    @DeleteMapping("/{id}")
    @RequiresPermission("usergroup:delete")
    public ApiResponse<Void> deleteUserGroup(@PathVariable Long id) {
        log.debug("=== 删除用户组接口调用开始 ===");
        log.info("删除用户组请求 - 用户组ID: {}", id);
        try {
            userGroupService.deleteUserGroup(id);
            log.info("删除用户组成功 - 用户组ID: {}", id);
            log.debug("=== 删除用户组接口调用结束 ===");
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("删除用户组失败 - 用户组ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取所有用户组
     */
    @GetMapping
    @RequiresPermission("usergroup:view")
    public ApiResponse<List<UserGroup>> getAllUserGroups() {
        log.debug("=== 获取所有用户组接口调用开始 ===");
        try {
            List<UserGroup> groups = userGroupService.getAllUserGroups();
            log.info("获取所有用户组成功 - 用户组数量: {}", groups.size());
            log.debug("=== 获取所有用户组接口调用结束 ===");
            return ApiResponse.success(groups);
        } catch (Exception e) {
            log.error("获取所有用户组失败 - 错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取用户组详情
     */
    @GetMapping("/{id}")
    @RequiresPermission("usergroup:view")
    public ApiResponse<UserGroup> getUserGroupById(@PathVariable Long id) {
        log.debug("=== 获取用户组详情接口调用开始 ===");
        log.info("获取用户组详情请求 - 用户组ID: {}", id);
        try {
            UserGroup group = userGroupService.getUserGroupById(id);
            log.info("获取用户组详情成功 - 用户组ID: {}, 用户组代码: {}, 用户组名称: {}", 
                    group.getId(), group.getGroupCode(), group.getGroupName());
            log.debug("=== 获取用户组详情接口调用结束 ===");
            return ApiResponse.success(group);
        } catch (Exception e) {
            log.error("获取用户组详情失败 - 用户组ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 添加用户到用户组
     */
    @PostMapping("/{id}/members")
    @RequiresPermission("usergroup:manage")
    public ApiResponse<Void> addMembers(@PathVariable Long id, @RequestBody AddMembersRequest request) {
        log.debug("=== 添加用户到用户组接口调用开始 ===");
        log.info("添加用户到用户组请求 - 用户组ID: {}, 用户数量: {}", id, request.getUserIds().size());
        try {
            userGroupService.addMembersToGroup(id, request.getUserIds());
            log.info("添加用户到用户组成功 - 用户组ID: {}, 用户数量: {}", id, request.getUserIds().size());
            log.debug("=== 添加用户到用户组接口调用结束 ===");
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("添加用户到用户组失败 - 用户组ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 从用户组移除用户
     */
    @DeleteMapping("/{id}/members")
    @RequiresPermission("usergroup:manage")
    public ApiResponse<Void> removeMembers(@PathVariable Long id, @RequestBody RemoveMembersRequest request) {
        log.debug("=== 从用户组移除用户接口调用开始 ===");
        log.info("从用户组移除用户请求 - 用户组ID: {}, 用户数量: {}", id, request.getUserIds().size());
        try {
            userGroupService.removeMembersFromGroup(id, request.getUserIds());
            log.info("从用户组移除用户成功 - 用户组ID: {}, 用户数量: {}", id, request.getUserIds().size());
            log.debug("=== 从用户组移除用户接口调用结束 ===");
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("从用户组移除用户失败 - 用户组ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取用户组的所有成员
     */
    @GetMapping("/{id}/members")
    @RequiresPermission("usergroup:view")
    public ApiResponse<List<User>> getGroupMembers(@PathVariable Long id) {
        log.debug("=== 获取用户组成员接口调用开始 ===");
        log.info("获取用户组成员请求 - 用户组ID: {}", id);
        try {
            List<User> members = userGroupService.getGroupMembers(id);
            log.info("获取用户组成员成功 - 用户组ID: {}, 成员数量: {}", id, members.size());
            log.debug("=== 获取用户组成员接口调用结束 ===");
            return ApiResponse.success(members);
        } catch (Exception e) {
            log.error("获取用户组成员失败 - 用户组ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取用户所属的所有用户组
     */
    @GetMapping("/user/{userId}")
    @RequiresPermission("usergroup:view")
    public ApiResponse<List<UserGroup>> getUserGroups(@PathVariable Long userId) {
        log.debug("=== 获取用户所属用户组接口调用开始 ===");
        log.info("获取用户所属用户组请求 - 用户ID: {}", userId);
        try {
            List<UserGroup> groups = userGroupService.getUserGroups(userId);
            log.info("获取用户所属用户组成功 - 用户ID: {}, 用户组数量: {}", userId, groups.size());
            log.debug("=== 获取用户所属用户组接口调用结束 ===");
            return ApiResponse.success(groups);
        } catch (Exception e) {
            log.error("获取用户所属用户组失败 - 用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            throw e;
        }
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

