package com.zsx.cstfilemanage.infrastructure.security;

import com.zsx.cstfilemanage.application.service.PermissionService;
import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 权限切面
 */
@Aspect
@Component
@Slf4j
public class PermissionAspect {

    private final PermissionService permissionService;

    public PermissionAspect(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 权限检查
     */
    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) {
        String permissionCode = requiresPermission.value();
        
        if (!permissionService.hasPermission(permissionCode)) {
            log.warn("用户 {} 没有权限: {}", SecurityContext.getCurrentUserId(), permissionCode);
            throw new BizException(new ErrorCode(1031, "没有权限: " + permissionCode));
        }
    }
}

