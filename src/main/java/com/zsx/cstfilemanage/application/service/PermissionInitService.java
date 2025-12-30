package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.domain.model.entity.Permission;
import com.zsx.cstfilemanage.domain.model.entity.Role;
import com.zsx.cstfilemanage.domain.model.entity.User;
import com.zsx.cstfilemanage.domain.model.entity.UserRole;
import com.zsx.cstfilemanage.domain.repository.PermissionRepository;
import com.zsx.cstfilemanage.domain.repository.RoleRepository;
import com.zsx.cstfilemanage.domain.repository.UserRepository;
import com.zsx.cstfilemanage.domain.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 权限初始化服务
 * 系统启动时自动初始化基础权限和角色
 */
@Component
@Slf4j
public class PermissionInitService implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;
    private final RoleService roleService;

    public PermissionInitService(PermissionRepository permissionRepository,
                                 RoleRepository roleRepository,
                                 PermissionService permissionService,
                                 RoleService roleService,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 UserRoleRepository userRoleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
//        log.info("开始初始化权限和角色...");
//        initPermissions();
//        initRoles();
//        initUsers();
//        log.info("权限和角色初始化完成");
    }

    /**
     * 初始化权限
     */
    private void initPermissions() {
        Map<String, Permission> permissionMap = new HashMap<>();

        // 文档管理权限
        createPermissionIfNotExists("document:upload", "文档上传", "上传文档权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("document:view", "文档查看", "查看文档权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("document:download", "文档下载", "下载文档权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("document:delete", "文档删除", "删除文档权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("document:modify", "文档修改", "修改文档权限", "OPERATION", null, permissionMap);

        // 审批权限
        createPermissionIfNotExists("approval:submit", "提交审批", "提交审批权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("approval:approve", "审批文档", "审批文档权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("approval:view", "查看审批", "查看审批进度权限", "OPERATION", null, permissionMap);

        // 文件下发权限
        createPermissionIfNotExists("distribution:distribute", "文件下发", "下发文件权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("distribution:recall", "文件回收", "回收文件权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("distribution:obsolete", "文件作废", "作废文件权限", "OPERATION", null, permissionMap);

        // 版本管理权限
        createPermissionIfNotExists("version:create", "创建版本", "创建新版本权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("version:view", "查看版本", "查看版本历史权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("version:compare", "版本对比", "版本对比权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("version:restore", "恢复版本", "恢复历史版本权限", "OPERATION", null, permissionMap);

        // 用户管理权限
        createPermissionIfNotExists("user:create", "创建用户", "创建用户权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("user:update", "更新用户", "更新用户权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("user:delete", "删除用户", "删除用户权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("user:view", "查看用户", "查看用户权限", "OPERATION", null, permissionMap);

        // 角色管理权限
        createPermissionIfNotExists("role:create", "创建角色", "创建角色权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("role:update", "更新角色", "更新角色权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("role:delete", "删除角色", "删除角色权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("role:view", "查看角色", "查看角色权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("role:assign", "分配角色", "分配角色权限", "OPERATION", null, permissionMap);

        // 权限管理权限
        createPermissionIfNotExists("permission:create", "创建权限", "创建权限权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("permission:update", "更新权限", "更新权限权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("permission:view", "查看权限", "查看权限权限", "OPERATION", null, permissionMap);

        // 用户组管理权限
        createPermissionIfNotExists("usergroup:create", "创建用户组", "创建用户组权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("usergroup:update", "更新用户组", "更新用户组权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("usergroup:delete", "删除用户组", "删除用户组权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("usergroup:view", "查看用户组", "查看用户组权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("usergroup:manage", "管理用户组", "管理用户组成员权限", "OPERATION", null, permissionMap);

        // 日志管理权限
        createPermissionIfNotExists("log:view", "查看日志", "查看操作日志权限", "OPERATION", null, permissionMap);
        createPermissionIfNotExists("log:export", "导出日志", "导出日志权限", "OPERATION", null, permissionMap);
    }

    /**
     * 创建权限（如果不存在）
     */
    private void createPermissionIfNotExists(String code, String name, String description,
                                             String type, Long parentId, Map<String, Permission> permissionMap) {
        if (permissionRepository.findByPermissionCode(code) == null) {
            Permission permission = new Permission();
            permission.setPermissionCode(code);
            permission.setPermissionName(name);
            permission.setDescription(description);
            permission.setPermissionType(type);
            permission.setParentId(parentId);
            permissionRepository.save(permission);
            permissionMap.put(code, permission);
            log.info("创建权限: {}", code);
        }
    }

    /**
     * 初始化角色
     */
    private void initRoles() {
        // 系统管理员角色
        Role adminRole = createRoleIfNotExists("ADMIN", "系统管理员", "拥有所有权限");

        // 设计人员角色
        Role designerRole = createRoleIfNotExists("DESIGNER", "设计人员", "设计人员角色");

        // 审批人员角色
        Role approverRole = createRoleIfNotExists("APPROVER", "审批人员", "审批人员角色");

        // 普通查看人员角色
        Role viewerRole = createRoleIfNotExists("VIEWER", "普通查看人员", "只能查看文档");

        // 为角色分配权限
        assignPermissionsToRole(adminRole, ".*"); // 管理员拥有所有权限
        assignPermissionsToRole(designerRole,
                "document:upload", "document:view", "document:download",
                "document:modify", "approval:submit", "version:create", "version:view");

        assignPermissionsToRole(approverRole,
                "document:view", "approval:approve", "approval:view");

        assignPermissionsToRole(viewerRole,
                "document:view", "document:download", "version:view");
    }

    /**
     * 初始化角色
     */
    private void initUsers() {
        User user = new User();
        user.setUsername("admin");
        Optional<User> user1 = userRepository.findByUsername(user.getUsername());
        if (user1.isPresent()) {
            // 用户存在，直接返回
            return;
        }
        user.setPassword(passwordEncoder.encode("admin"));
        user.setDepartmentId(1L);
        user.setDepartmentName("系统管理部");
        user.setEmail("admin@example.com");
        user.setEnabled(Boolean.TRUE);
        user.setPhone("13800000000");
        user.setPosition("管理员");
        user.setRealName("系统管理员");
        userRepository.save(user);
        UserRole userRole = new UserRole();
        userRole.setRoleId(1L);
        userRole.setUserId(user.getId());
        userRoleRepository.save(userRole);
    }

    /**
     * 创建角色（如果不存在）
     */
    private Role createRoleIfNotExists(String code, String name, String description) {
        return roleRepository.findByRoleCode(code).orElseGet(() -> {
            Role role = new Role();
            role.setRoleCode(code);
            role.setRoleName(name);
            role.setDescription(description);
            role.setEnabled(true);
            Role saved = roleRepository.save(role);
            log.info("创建角色: {}", code);
            return saved;
        });
    }

    /**
     * 为角色分配权限
     */
    private void assignPermissionsToRole(Role role, String... permissionCodes) {
        if (permissionCodes.length == 1 && ".*".equals(permissionCodes[0])) {
            // 管理员角色，分配所有权限
            java.util.List<Permission> allPermissions = permissionRepository.findAll();
            java.util.List<Long> permissionIds = allPermissions.stream()
                    .map(Permission::getId)
                    .toList();
            permissionService.assignPermissionsToRole(role.getId(), permissionIds);
        } else {
            // 普通角色，分配指定权限
            java.util.List<Long> permissionIds = java.util.Arrays.stream(permissionCodes)
                    .map(code -> permissionRepository.findByPermissionCode(code))
                    .filter(p -> p != null)
                    .map(Permission::getId)
                    .toList();
            if (!permissionIds.isEmpty()) {
                permissionService.assignPermissionsToRole(role.getId(), permissionIds);
            }
        }
    }
}

