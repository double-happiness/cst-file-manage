# 新增功能说明

## 1. 认证授权功能 ✅

### 实现内容
- **Spring Security集成**：完整的JWT认证机制
- **登录认证**：用户名密码登录，返回JWT Token
- **权限拦截**：基于JWT Token的请求拦截和用户上下文设置
- **密码加密**：使用BCrypt加密存储

### 相关文件
- `JwtTokenProvider.java` - JWT Token生成和验证
- `JwtAuthenticationFilter.java` - JWT认证过滤器
- `SecurityConfig.java` - Spring Security配置
- `AuthService.java` - 认证服务
- `AuthController.java` - 登录接口

### API接口
- `POST /api/v1/auth/login` - 用户登录

### 配置说明
在 `application.yml` 中配置JWT密钥：
```yaml
jwt:
  secret: your-secret-key-should-be-at-least-256-bits-long-for-security
  expiration: 86400000  # 24小时
```

### 使用方式
1. 调用登录接口获取Token
2. 在后续请求的Header中添加：`Authorization: Bearer {token}`

---

## 2. 完善审批流程 ✅

### 实现内容
- **角色自动分配**：根据审批流程配置的角色，自动查找拥有该角色的用户作为审批人
- **多级审批流转**：审批通过后自动流转到下一环节，并通知下一环节审批人
- **审批通知**：审批通过、驳回时自动通知相关人员

### 相关文件
- `ApprovalService.java` - 审批服务（已完善）
- `UserRoleRepository.java` - 用户角色关联查询

### 核心改进
1. `findApproverByRole()` 方法：根据角色ID查询拥有该角色的用户
2. 审批通过后自动查找下一环节审批人并发送通知
3. 审批驳回时通知上传人

---

## 3. 通知功能 ✅

### 实现内容
- **邮件通知**：支持发送邮件通知（审批、下发等）
- **短信通知**：支持短信通知（可配置启用/禁用）
- **通知场景**：
  - 审批通知（待审批、审批通过、审批驳回）
  - 文件下发通知

### 相关文件
- `NotificationService.java` - 通知服务
- `SmsService.java` - 短信服务（示例实现）

### 配置说明
在 `application.yml` 中配置：
```yaml
spring:
  mail:
    host: smtp.example.com
    port: 587
    username: your-email@example.com
    password: your-password
    from: noreply@example.com

notification:
  email:
    enabled: true
  sms:
    enabled: false

sms:
  provider: mock  # mock 或实际服务商名称
  api-key: ""
```

### 通知场景
- 文档提交审批 → 通知第一个审批人
- 审批通过 → 通知上传人
- 审批驳回 → 通知上传人（包含驳回原因）
- 文件下发 → 通知所有接收人

---

## 4. 文件预览功能 ✅

### 实现内容
- **PDF预览**：PDF文件第一页预览图生成
- **图片预览**：JPEG、PNG图片直接预览
- **通用文件下载**：支持所有文件类型的下载

### 相关文件
- `FilePreviewService.java` - 文件预览服务
- `FilePreviewController.java` - 文件预览接口

### API接口
- `GET /api/v1/preview/{documentId}` - 预览文件（下载）
- `GET /api/v1/preview/pdf/{documentId}` - PDF预览（返回第一页图片）
- `GET /api/v1/preview/image/{documentId}` - 图片预览

### 技术实现
- 使用Apache PDFBox进行PDF文本提取和图片渲染
- PDF第一页渲染为PNG图片返回

---

## 5. 文件对比功能 ✅

### 实现内容
- **PDF对比**：提取PDF文本内容进行对比
- **文本文件对比**：支持文本类文件的逐行对比
- **差异高亮**：使用difflib库进行差异分析，显示详细的修改内容

### 相关文件
- `FileCompareService.java` - 文件对比服务
- `VersionService.java` - 版本服务（已集成对比功能）

### API接口
- `GET /api/v1/versions/compare?version1Id={id}&version2Id={id}` - 版本对比

### 对比结果
返回详细的差异信息，包括：
- 差异位置
- 差异类型（插入、删除、修改）
- 原文内容
- 修改后内容

### 技术实现
- 使用Apache PDFBox提取PDF文本
- 使用difflib库进行文本差异分析
- 支持PDF和文本类文件对比

---

## 6. 日志导出Excel功能 ✅

### 实现内容
- **Excel导出**：将操作日志导出为Excel格式
- **多条件筛选**：支持按用户、操作类型、时间范围筛选
- **格式化输出**：包含标题行、数据格式化、自动列宽调整

### 相关文件
- `LogExportService.java` - 日志导出服务
- `OperationLogController.java` - 日志控制器（已添加导出接口）

### API接口
- `GET /api/v1/logs/export?userId={id}&operationType={type}&startTime={time}&endTime={time}` - 导出日志

### 导出内容
Excel包含以下列：
- ID
- 操作人
- 操作类型
- 操作内容
- 操作对象类型
- 操作对象ID
- 操作结果
- 错误信息
- IP地址
- 操作时间

### 技术实现
- 使用Apache POI生成Excel文件
- 支持样式设置（标题行加粗、背景色等）
- 自动调整列宽

---

## 依赖说明

新增的Maven依赖：
```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- Spring Mail -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Apache POI for Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>

<!-- PDF处理 -->
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.1</version>
</dependency>

<!-- 文件对比 -->
<dependency>
    <groupId>com.github.difflib</groupId>
    <artifactId>difflib</artifactId>
    <version>1.4</version>
</dependency>
```

---

## 使用建议

1. **认证授权**：
   - 生产环境请修改JWT密钥
   - 建议实现Token刷新机制
   - 可以添加权限注解控制接口访问

2. **通知功能**：
   - 配置真实的邮件服务器信息
   - 如需短信功能，对接实际的短信服务商（阿里云、腾讯云等）

3. **文件预览**：
   - PDF预览功能对内存有一定要求，大文件建议异步处理
   - 可以添加缓存机制提高性能

4. **文件对比**：
   - 大文件对比可能耗时较长，建议异步处理
   - 可以添加对比结果缓存

5. **日志导出**：
   - 大量日志导出可能占用较多内存，建议分批处理
   - 可以添加导出任务队列

---

## 测试建议

1. 测试登录接口获取Token
2. 测试使用Token访问需要认证的接口
3. 测试审批流程的自动分配和通知
4. 测试邮件通知功能（配置邮件服务器）
5. 测试文件预览功能（上传PDF和图片）
6. 测试文件对比功能（上传两个版本的文档）
7. 测试日志导出功能（生成Excel文件）

