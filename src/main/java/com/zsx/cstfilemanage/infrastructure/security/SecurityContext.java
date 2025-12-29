package com.zsx.cstfilemanage.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SecurityContext {

    // ThreadLocal 存储当前用户信息
    private static final ThreadLocal<UserInfo> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前用户信息（通常在拦截器/过滤器中调用）
     */
    public static void setCurrentUser(UserInfo user) {
        CONTEXT.set(user);
    }

    /**
     * 获取当前用户信息
     */
    public static UserInfo getCurrentUser() {
        return CONTEXT.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        UserInfo user = CONTEXT.get();
        return user == null ? null : user.getUserId();
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUserName() {
        UserInfo user = CONTEXT.get();
        return user == null ? null : (user.getRealName() != null ? user.getRealName() : user.getUsername());
    }

    /**
     * 清理当前线程上下文
     */
    public static void clear() {
        CONTEXT.remove();
    }

    // 业务实体类（可放公共模块）
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String username;
        private String realName;
        private String tenantId; // 多租户可选
    }
}
