# 前端项目使用说明

## 项目结构

```
frontend/
├── src/
│   ├── api/              # API接口封装（11个文件）
│   ├── pages/            # 页面组件（10个页面）
│   ├── layout/           # 布局组件
│   ├── router/           # 路由配置
│   ├── store/            # 状态管理
│   └── App.tsx           # 根组件
├── package.json
├── tsconfig.json
├── vite.config.ts
└── index.html
```

## 已创建的页面

### 1. 认证模块
- ✅ `LoginPage.tsx` - 登录页面

### 2. 文档管理模块
- ✅ `DocumentListPage.tsx` - 文档列表（搜索、筛选、分页）
- ✅ `DocumentUploadPage.tsx` - 文档上传
- ✅ `DocumentDetailPage.tsx` - 文档详情（支持提交审批）

### 3. 审批管理模块
- ✅ `ApprovalTodoPage.tsx` - 待审批列表
- ✅ `ApprovalHistoryPage.tsx` - 审批历史

### 4. 文件下发模块
- ✅ `DistributionPage.tsx` - 文件下发（批量下发、回收、作废）

### 5. 版本管理模块
- ✅ `VersionListPage.tsx` - 版本列表
- ✅ `VersionComparePage.tsx` - 版本对比

### 6. 用户管理模块
- ✅ `UserListPage.tsx` - 用户列表（增删改、密码重置）

### 7. 角色管理模块
- ✅ `RoleListPage.tsx` - 角色列表（增删改、权限分配）

### 8. 权限管理模块
- ✅ `PermissionListPage.tsx` - 权限列表（增删改）

### 9. 用户组管理模块
- ✅ `UserGroupPage.tsx` - 用户组列表（增删改、成员管理）

### 10. 日志管理模块
- ✅ `LogListPage.tsx` - 日志查询（多条件筛选、导出Excel）

## 已创建的API接口封装

所有后端接口都已封装在 `src/api/` 目录：

1. **http.ts** - HTTP客户端（统一配置、拦截器）
2. **auth.ts** - 认证接口
3. **document.ts** - 文档接口
4. **approval.ts** - 审批接口
5. **distribution.ts** - 下发接口
6. **version.ts** - 版本接口
7. **user.ts** - 用户接口
8. **role.ts** - 角色接口
9. **permission.ts** - 权限接口
10. **userGroup.ts** - 用户组接口
11. **log.ts** - 日志接口
12. **preview.ts** - 预览接口
13. **types.ts** - TypeScript类型定义

## 快速开始

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
```

## 功能特性

### ✅ 已实现的功能

1. **完整的API对接**
   - 所有后端接口都已封装
   - 统一的错误处理
   - 自动Token管理

2. **用户认证**
   - 登录/登出
   - Token持久化存储
   - 路由守卫

3. **文档管理**
   - 文档列表（搜索、筛选、分页）
   - 文档上传（表单填写）
   - 文档详情查看
   - 文档预览

4. **审批流程**
   - 待审批列表
   - 审批操作（同意、驳回、修改后再审）
   - 审批历史查询

5. **文件下发**
   - 批量文件下发
   - 下发对象选择（用户、用户组、部门、岗位）
   - 文件回收和作废

6. **版本管理**
   - 版本列表
   - 版本对比
   - 版本恢复

7. **用户管理**
   - 用户列表
   - 新增/编辑用户
   - 密码重置

8. **角色权限管理**
   - 角色管理（增删改、权限分配）
   - 权限管理（增删改）
   - 用户组管理（增删改、成员管理）

9. **日志管理**
   - 日志查询（多条件筛选）
   - 日志导出Excel

## 配置说明

### 后端API地址

在 `vite.config.ts` 中配置了代理：
```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  },
}
```

如需修改后端地址，请更新此配置。

### Token管理

- Token存储在localStorage中
- 请求时自动添加到Header：`Authorization: Bearer {token}`
- 401错误自动跳转登录页

## 注意事项

1. **API路径**：所有API路径都以 `/api` 开头，会被代理到后端
2. **分页**：后端使用Spring Data分页（page从0开始），前端已做转换
3. **日期格式**：使用dayjs处理日期，格式化为中文格式
4. **文件上传**：使用FormData，支持拖拽上传
5. **错误处理**：统一的错误提示和401自动跳转

## 待完善的功能

1. **文件预览**：PDF预览功能需要后端支持
2. **版本对比**：需要完善对比结果的展示
3. **审批流程**：需要完善审批流程的可视化展示
4. **权限控制**：前端可以根据用户权限隐藏/显示功能按钮
5. **数据导出**：可以添加更多数据导出功能

## 开发建议

1. 根据实际后端接口调整API路径和参数
2. 根据实际业务需求调整页面布局和交互
3. 添加更多表单验证规则
4. 优化表格分页和搜索性能
5. 添加更多错误处理和用户提示
6. 可以添加国际化支持（i18n）

