# RBAC权限管理和用户组管理功能说明

## 1. RBAC权限管理功能 ✅

### 1.1 核心功能

#### 权限管理
- ✅ **权限创建**：支持创建功能模块权限和操作权限
- ✅ **权限更新**：支持更新权限信息
- ✅ **权限查询**：支持查询所有权限、权限树结构
- ✅ **权限检查**：支持检查用户是否拥有指定权限
- ✅ **用户权限查询**：支持查询用户的所有权限

#### 角色管理
- ✅ **角色创建**：支持创建角色并设置角色信息
- ✅ **角色更新**：支持更新角色信息
- ✅ **角色删除**：支持删除角色（会检查是否被使用）
- ✅ **角色查询**：支持查询所有角色、角色详情
- ✅ **角色权限分配**：支持为角色分配/移除权限
- ✅ **用户角色分配**：支持为用户分配/移除角色

#### 权限拦截
- ✅ **权限注解**：`@RequiresPermission` 注解标记需要权限的方法
- ✅ **AOP切面**：自动拦截并检查权限
- ✅ **权限验证**：无权限时自动抛出异常

### 1.2 相关文件

#### 服务层
- `PermissionService.java` - 权限管理服务
- `RoleService.java` - 角色管理服务
- `PermissionInitService.java` - 权限初始化服务（系统启动时自动初始化）

#### 控制器层
- `PermissionController.java` - 权限管理接口
- `RoleController.java` - 角色管理接口

#### 安全层
- `RequiresPermission.java` - 权限注解
- `PermissionAspect.java` - 权限切面

#### 仓储层
- `RolePermissionRepository.java` - 角色权限关联查询
- `UserRoleRepository.java` - 用户角色关联查询（已存在）

### 1.3 API接口

#### 权限管理接口
- `POST /api/v1/permissions` - 创建权限（需要权限：permission:create）
- `PUT /api/v1/permissions/{id}` - 更新权限（需要权限：permission:update）
- `GET /api/v1/permissions` - 获取所有权限（需要权限：permission:view）
- `GET /api/v1/permissions/tree` - 获取权限树（需要权限：permission:view）
- `GET /api/v1/permissions/my` - 获取当前用户的权限

#### 角色管理接口
- `POST /api/v1/roles` - 创建角色（需要权限：role:create）
- `PUT /api/v1/roles/{id}` - 更新角色（需要权限：role:update）
- `DELETE /api/v1/roles/{id}` - 删除角色（需要权限：role:delete）
- `GET /api/v1/roles` - 获取所有角色（需要权限：role:view）
- `GET /api/v1/roles/{id}` - 获取角色详情（需要权限：role:view）
- `POST /api/v1/roles/{id}/permissions` - 为角色分配权限（需要权限：role:assign）
- `GET /api/v1/roles/{id}/permissions` - 获取角色的权限（需要权限：role:view）
- `POST /api/v1/roles/assign` - 为用户分配角色（需要权限：role:assign）
- `GET /api/v1/roles/user/{userId}` - 获取用户的角色（需要权限：role:view）

### 1.4 权限代码列表

系统自动初始化以下权限：

#### 文档管理权限
- `document:upload` - 文档上传
- `document:view` - 文档查看
- `document:download` - 文档下载
- `document:delete` - 文档删除
- `document:modify` - 文档修改

#### 审批权限
- `approval:submit` - 提交审批
- `approval:approve` - 审批文档
- `approval:view` - 查看审批

#### 文件下发权限
- `distribution:distribute` - 文件下发
- `distribution:recall` - 文件回收
- `distribution:obsolete` - 文件作废

#### 版本管理权限
- `version:create` - 创建版本
- `version:view` - 查看版本
- `version:compare` - 版本对比
- `version:restore` - 恢复版本

#### 用户管理权限
- `user:create` - 创建用户
- `user:update` - 更新用户
- `user:delete` - 删除用户
- `user:view` - 查看用户

#### 角色管理权限
- `role:create` - 创建角色
- `role:update` - 更新角色
- `role:delete` - 删除角色
- `role:view` - 查看角色
- `role:assign` - 分配角色

#### 权限管理权限
- `permission:create` - 创建权限
- `permission:update` - 更新权限
- `permission:view` - 查看权限

#### 用户组管理权限
- `usergroup:create` - 创建用户组
- `usergroup:update` - 更新用户组
- `usergroup:delete` - 删除用户组
- `usergroup:view` - 查看用户组
- `usergroup:manage` - 管理用户组

#### 日志管理权限
- `log:view` - 查看日志
- `log:export` - 导出日志

### 1.5 预定义角色

系统自动初始化以下角色：

#### 系统管理员（ADMIN）
- 拥有所有权限

#### 设计人员（DESIGNER）
- 文档上传、查看、下载、修改
- 提交审批
- 创建版本、查看版本

#### 审批人员（APPROVER）
- 文档查看
- 审批文档、查看审批

#### 普通查看人员（VIEWER）
- 文档查看、下载
- 查看版本

### 1.6 使用示例

#### 在Controller方法上使用权限注解
```java
@PostMapping("/upload")
@RequiresPermission("document:upload")
public ApiResponse<DocumentResponse> uploadDocument(...) {
    // 只有拥有 document:upload 权限的用户才能访问
}
```

#### 在Service中检查权限
```java
if (!permissionService.hasPermission("document:upload")) {
    throw new BizException(new ErrorCode(1031, "没有权限"));
}
```

---

## 2. 用户组管理功能 ✅

### 2.1 核心功能

- ✅ **用户组创建**：支持创建用户组并设置基本信息
- ✅ **用户组更新**：支持更新用户组信息
- ✅ **用户组删除**：支持删除用户组（会自动删除成员关联）
- ✅ **用户组查询**：支持查询所有用户组、用户组详情
- ✅ **成员管理**：支持添加/移除用户组成员
- ✅ **成员查询**：支持查询用户组的所有成员
- ✅ **用户组查询**：支持查询用户所属的所有用户组

### 2.2 相关文件

#### 服务层
- `UserGroupService.java` - 用户组管理服务

#### 控制器层
- `UserGroupController.java` - 用户组管理接口

#### 仓储层
- `UserGroupMemberRepository.java` - 用户组成员关联查询

### 2.3 API接口

- `POST /api/v1/user-groups` - 创建用户组（需要权限：usergroup:create）
- `PUT /api/v1/user-groups/{id}` - 更新用户组（需要权限：usergroup:update）
- `DELETE /api/v1/user-groups/{id}` - 删除用户组（需要权限：usergroup:delete）
- `GET /api/v1/user-groups` - 获取所有用户组（需要权限：usergroup:view）
- `GET /api/v1/user-groups/{id}` - 获取用户组详情（需要权限：usergroup:view）
- `POST /api/v1/user-groups/{id}/members` - 添加成员到用户组（需要权限：usergroup:manage）
- `DELETE /api/v1/user-groups/{id}/members` - 从用户组移除成员（需要权限：usergroup:manage）
- `GET /api/v1/user-groups/{id}/members` - 获取用户组的所有成员（需要权限：usergroup:view）
- `GET /api/v1/user-groups/user/{userId}` - 获取用户所属的所有用户组（需要权限：usergroup:view）

### 2.4 集成功能

用户组已集成到文件下发功能中：
- 文件下发时可以选择用户组作为下发对象
- 系统会自动查找用户组的所有成员并下发文件

---

## 3. 使用说明

### 3.1 初始化

系统启动时会自动：
1. 创建所有基础权限
2. 创建预定义角色
3. 为角色分配权限

### 3.2 权限配置流程

1. **创建权限**（如果需要新权限）
   ```bash
   POST /api/v1/permissions
   {
     "permissionCode": "custom:permission",
     "permissionName": "自定义权限",
     "description": "自定义权限描述",
     "permissionType": "OPERATION"
   }
   ```

2. **创建角色**（如果需要新角色）
   ```bash
   POST /api/v1/roles
   {
     "roleCode": "CUSTOM_ROLE",
     "roleName": "自定义角色",
     "description": "自定义角色描述"
   }
   ```

3. **为角色分配权限**
   ```bash
   POST /api/v1/roles/{roleId}/permissions
   {
     "permissionIds": [1, 2, 3]
   }
   ```

4. **为用户分配角色**
   ```bash
   POST /api/v1/roles/assign
   {
     "userId": 1,
     "roleIds": [1, 2]
   }
   ```

### 3.3 用户组使用流程

1. **创建用户组**
   ```bash
   POST /api/v1/user-groups
   {
     "groupCode": "PROJECT_A",
     "groupName": "项目A组",
     "description": "项目A的所有成员"
   }
   ```

2. **添加成员到用户组**
   ```bash
   POST /api/v1/user-groups/{groupId}/members
   {
     "userIds": [1, 2, 3]
   }
   ```

3. **在文件下发时使用用户组**
   ```bash
   POST /api/v1/distributions/distribute
   {
     "documentIds": [1, 2],
     "targetType": "USER_GROUP",
     "targetIds": [1],
     "targetNames": ["项目A组"]
   }
   ```

---

## 4. 技术实现

### 4.1 权限检查机制

1. **注解方式**：使用 `@RequiresPermission` 注解
2. **AOP切面**：`PermissionAspect` 自动拦截
3. **权限验证**：调用 `PermissionService.hasPermission()` 检查
4. **异常处理**：无权限时抛出 `BizException`

### 4.2 权限查询优化

- 使用 `Set` 存储用户权限，提高查询效率
- 通过角色关联查询，避免重复查询
- 支持权限缓存（可扩展）

### 4.3 用户组查询

- 支持双向查询：用户组→成员，用户→用户组
- 使用 `@Query` 注解优化查询性能
- 支持批量操作

---

## 5. 注意事项

1. **权限初始化**：系统启动时自动初始化，无需手动操作
2. **权限代码唯一性**：权限代码必须唯一
3. **角色删除限制**：被使用的角色无法删除
4. **用户组删除**：删除用户组会自动删除所有成员关联
5. **权限注解**：需要在方法上使用 `@RequiresPermission` 注解才会生效
6. **AOP依赖**：需要添加 `spring-boot-starter-aop` 依赖

---

## 6. 扩展建议

1. **权限缓存**：可以添加Redis缓存提高权限查询性能
2. **权限继承**：可以实现权限树结构的权限继承
3. **动态权限**：可以实现基于资源的动态权限控制
4. **权限审计**：可以记录权限变更日志
5. **批量操作**：可以添加批量分配权限/角色的接口

