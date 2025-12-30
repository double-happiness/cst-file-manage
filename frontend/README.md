# 文档管理系统 - 前端

基于 React + TypeScript + Ant Design + Vite 开发的文档管理系统前端。

## 技术栈

- **React 18** - UI框架
- **TypeScript** - 类型安全
- **Ant Design 5** - UI组件库
- **Vite** - 构建工具
- **React Router 6** - 路由管理
- **Zustand** - 状态管理
- **Axios** - HTTP客户端

## 功能模块

### 1. 认证授权
- ✅ 用户登录
- ✅ Token管理
- ✅ 路由守卫

### 2. 文档管理
- ✅ 文档列表（搜索、筛选、分页）
- ✅ 文档上传（拖拽上传、表单填写）
- ✅ 文档详情查看
- ✅ 文档预览

### 3. 审批管理
- ✅ 待审批列表
- ✅ 审批操作（同意、驳回、修改后再审）
- ✅ 审批历史查询
- ✅ 审批进度查看

### 4. 文件下发
- ✅ 批量文件下发
- ✅ 下发对象选择（用户、用户组、部门、岗位）
- ✅ 文件回收
- ✅ 文件作废

### 5. 版本管理
- ✅ 版本列表
- ✅ 版本对比
- ✅ 版本恢复

### 6. 用户管理
- ✅ 用户列表
- ✅ 新增/编辑用户
- ✅ 密码重置

### 7. 角色管理
- ✅ 角色列表
- ✅ 新增/编辑/删除角色
- ✅ 角色权限分配

### 8. 权限管理
- ✅ 权限列表
- ✅ 权限树展示
- ✅ 新增/编辑权限

### 9. 用户组管理
- ✅ 用户组列表
- ✅ 新增/编辑/删除用户组
- ✅ 成员管理

### 10. 操作日志
- ✅ 日志查询（多条件筛选）
- ✅ 日志导出Excel

## 快速开始

### 安装依赖

```bash
cd frontend
npm install
```

### 开发运行

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

## 项目结构

```
frontend/
├── src/
│   ├── api/              # API接口封装
│   │   ├── http.ts       # HTTP客户端配置
│   │   ├── auth.ts       # 认证接口
│   │   ├── document.ts  # 文档接口
│   │   ├── approval.ts   # 审批接口
│   │   ├── distribution.ts # 下发接口
│   │   ├── version.ts    # 版本接口
│   │   ├── user.ts       # 用户接口
│   │   ├── role.ts       # 角色接口
│   │   ├── permission.ts # 权限接口
│   │   ├── userGroup.ts  # 用户组接口
│   │   ├── log.ts        # 日志接口
│   │   └── preview.ts    # 预览接口
│   ├── pages/            # 页面组件
│   │   ├── auth/         # 认证页面
│   │   ├── document/     # 文档页面
│   │   ├── approval/     # 审批页面
│   │   ├── distribution/ # 下发页面
│   │   ├── version/      # 版本页面
│   │   ├── user/         # 用户页面
│   │   ├── role/         # 角色页面
│   │   ├── permission/   # 权限页面
│   │   ├── userGroup/    # 用户组页面
│   │   └── log/          # 日志页面
│   ├── layout/           # 布局组件
│   ├── router/           # 路由配置
│   ├── store/            # 状态管理
│   └── App.tsx           # 根组件
├── package.json
├── tsconfig.json
├── vite.config.ts
└── index.html
```

## API对接说明

所有API接口都已封装在 `src/api/` 目录下，统一使用 `/api` 作为基础路径。

### 接口路径映射

- 认证：`/api/v1/auth/*`
- 文档：`/api/v1/documents/*`
- 审批：`/api/v1/approvals/*`
- 下发：`/api/v1/distributions/*`
- 版本：`/api/v1/versions/*`
- 用户：`/api/v1/users/*`
- 角色：`/api/v1/roles/*`
- 权限：`/api/v1/permissions/*`
- 用户组：`/api/v1/user-groups/*`
- 日志：`/api/v1/logs/*`
- 预览：`/api/v1/preview/*`

### Token管理

登录成功后，Token会自动存储在localStorage中，并在后续请求中自动添加到请求头：
```
Authorization: Bearer {token}
```

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

## 注意事项

1. **登录状态**：Token存储在localStorage中，刷新页面不会丢失
2. **路由守卫**：未登录用户会自动跳转到登录页
3. **错误处理**：统一的错误提示和401自动跳转登录
4. **文件上传**：支持拖拽上传，文件大小限制50MB
5. **日期格式**：使用dayjs处理日期，格式化为中文格式

## 开发建议

1. 根据实际后端接口调整API路径和参数
2. 根据实际业务需求调整页面布局和交互
3. 添加更多表单验证规则
4. 优化表格分页和搜索性能
5. 添加更多错误处理和用户提示

