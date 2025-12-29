# 文档管理系统

基于Spring Boot 3.3.4开发的医疗器械文档管理系统，支持图纸/文件管理、审批流程、文件下发、版本管理等功能。

## 功能特性

### 1. 图纸/文件管理模块

#### 1.1 文件上传
- ✅ 支持常见医疗器械文件格式：CAD（.dwg、.dxf）、PDF、JPEG、PNG、Word、Excel等
- ✅ 支持文件拖拽上传和选择文件路径上传
- ✅ 上传时填写文件基本信息：文件名称、唯一编号、所属产品型号、版本号、编制日期、编制人、文件描述等
- ✅ 自动校验文件格式，不符合要求则拒绝上传
- ✅ 上传成功后自动生成缩略图（图片类型）

#### 1.2 文件审批
- ✅ 支持自定义审批流程配置
- ✅ 根据文件类型、重要程度等因素配置不同的审批环节和审批人员
- ✅ 文件上传完成后自动按照预设流程发送给第一个审批人
- ✅ 审批人可查看文件详情、预览内容，填写审批意见（同意、驳回、修改后再审）
- ✅ 实时查询审批进度
- ✅ 审批通过后流转至下一环节，全部通过后状态更新为"已批准"
- ✅ 审批被驳回时退回给上传人，可修改后重新提交
- ✅ 记录完整的审批轨迹

### 2. 文件下发、回收模块

#### 2.1 文件选择与下发对象设置
- ✅ 支持选择已批准的文档进行下发
- ✅ 支持批量下发多个文件
- ✅ 可设置下发对象：按部门、岗位、用户组或具体用户

#### 2.2 下发方式与记录
- ✅ 支持在线下发
- ✅ 可配置发送下发通知
- ✅ 自动记录文件下发的详细信息
- ✅ 接收人查看或下载文件后记录操作时间

#### 2.3 文件回收和作废
- ✅ 支持文件回收和作废操作
- ✅ 回收文件空间可查看回收时间和文件列表
- ✅ 作废文件空间可查看作废时间和文件列表

### 3. 文件版本管理模块

#### 3.1 版本创建与标识
- ✅ 基于当前版本创建新版本
- ✅ 自动生成新版本号（版本号规则可自定义）
- ✅ 填写版本变更说明：修改内容、修改原因、修改人、修改日期
- ✅ 清晰标识当前版本和历史版本

#### 3.2 版本追溯与对比
- ✅ 保存所有历史版本
- ✅ 可查看任意历史版本的文件内容及相关变更信息
- ✅ 支持不同版本之间的文件对比功能
- ✅ 提供版本历史记录查询功能

#### 3.3 版本控制与状态管理
- ✅ 只有"已批准"状态的文件才能作为当前有效版本被使用和下发
- ✅ 历史版本仅作为追溯依据，不可直接用于下发
- ✅ 恢复历史版本为当前有效版本需经过审批流程

### 4. 基础功能模块

#### 4.1 用户管理
- ✅ 支持新增、修改、删除用户账号
- ✅ 设置用户信息：姓名、所属部门、岗位、联系方式、登录密码等
- ✅ 用户分组管理（部门、项目等）
- ✅ 用户密码重置功能

#### 4.2 权限管理
- ✅ 基于角色的权限控制（RBAC）
- ✅ 创建不同角色：系统管理员、设计人员、审批人员、普通查看人员等
- ✅ 为每个角色分配相应的操作权限
- ✅ 权限包括：文件上传权、审批权、文件下发权、版本修改权、文件查看权、文件下载权等
- ✅ 支持权限的灵活调整

#### 4.3 日志管理
- ✅ 自动记录所有用户的操作日志
- ✅ 包括登录日志（登录时间、登录IP、登录状态等）
- ✅ 包括操作日志（操作人、操作时间、操作内容、操作对象、操作结果等）
- ✅ 提供日志查询和导出功能
- ✅ 日志数据至少保存3年

#### 4.4 搜索与筛选
- ✅ 强大的搜索功能：文件名称、编号、版本号、所属产品、上传人、审批状态等
- ✅ 支持多条件组合筛选
- ✅ 搜索结果支持按不同字段排序

## 技术栈

- **框架**: Spring Boot 3.3.4
- **Java版本**: 17
- **数据库**: MySQL 8.0+
- **ORM**: Spring Data JPA
- **缓存**: Redis
- **构建工具**: Maven
- **缩略图生成**: Thumbnailator

## 项目结构

```
src/main/java/com/zsx/cstfilemanage/
├── application/          # 应用层（服务编排）
│   └── service/         # 应用服务
├── domain/              # 领域层（核心业务）
│   ├── cenum/          # 枚举类型
│   ├── model/          # 领域模型
│   │   └── entity/     # 实体类
│   ├── policy/         # 业务策略
│   └── repository/     # 仓储接口
├── infrastructure/     # 基础设施层
│   ├── persistence/    # 持久化实现
│   ├── security/       # 安全相关
│   └── storage/       # 存储相关
└── interfaces/         # 接口层
    └── http/          # HTTP接口
        ├── controller/ # 控制器
        ├── request/   # 请求对象
        └── response/  # 响应对象
```

## 数据库配置

在 `application.yml` 中配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cst_file_manage?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

## API接口

### 文档管理
- `POST /api/v1/documents/upload` - 上传文档
- `GET /api/v1/documents/{id}` - 查询文档详情
- `GET /api/v1/documents/search` - 搜索文档

### 审批流程
- `POST /api/v1/approvals/submit/{documentId}` - 提交审批
- `POST /api/v1/approvals/approve/{documentId}` - 审批文档
- `GET /api/v1/approvals/progress/{documentId}` - 查询审批进度

### 文件下发
- `POST /api/v1/distributions/distribute` - 下发文件
- `POST /api/v1/distributions/recall` - 回收文件
- `POST /api/v1/distributions/obsolete` - 作废文件
- `POST /api/v1/distributions/view/{distributionId}` - 记录查看
- `POST /api/v1/distributions/download/{distributionId}` - 记录下载

### 版本管理
- `POST /api/v1/versions/create/{documentId}` - 创建新版本
- `GET /api/v1/versions/{fileNumber}` - 查询文档所有版本
- `POST /api/v1/versions/restore/{versionId}` - 恢复历史版本
- `GET /api/v1/versions/compare` - 版本对比

### 用户管理
- `POST /api/v1/users` - 创建用户
- `PUT /api/v1/users/{id}` - 更新用户
- `POST /api/v1/users/{id}/reset-password` - 重置密码
- `GET /api/v1/users` - 查询所有用户
- `GET /api/v1/users/{id}` - 查询用户详情

### 日志管理
- `GET /api/v1/logs` - 查询操作日志

## 使用说明

1. 配置数据库连接信息
2. 启动应用，数据库表会自动创建
3. 创建初始管理员用户
4. 配置审批流程
5. 开始使用文档管理功能

## 注意事项

- 文件上传目录默认为 `uploads/`，可根据需要修改
- 缩略图仅对图片类型（JPEG、PNG）生成
- 审批流程需要预先配置
- 日志数据保留3年，可配置清理策略

## 开发计划

- [ ] 实现文件内容对比功能（文本、PDF等）
- [ ] 实现邮件/短信通知功能
- [ ] 实现文件预览功能（PDF、图片等）
- [ ] 实现权限管理的完整RBAC功能
- [ ] 实现用户组管理功能
- [ ] 实现日志导出Excel功能

