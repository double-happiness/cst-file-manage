// 文档状态枚举
export enum DocumentStatus {
  DRAFT = 'DRAFT',
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  APPROVING = 'APPROVING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  RECALLED = 'RECALLED',
  OBSOLETE = 'OBSOLETE',
}

// 审批状态枚举
export enum ApprovalStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  MODIFY_REQUIRED = 'MODIFY_REQUIRED',
}

// 操作类型枚举
export enum OperationType {
  LOGIN = 'LOGIN',
  LOGOUT = 'LOGOUT',
  UPLOAD = 'UPLOAD',
  DOWNLOAD = 'DOWNLOAD',
  APPROVE = 'APPROVE',
  REJECT = 'REJECT',
  DISTRIBUTE = 'DISTRIBUTE',
  RECALL = 'RECALL',
  OBSOLETE = 'OBSOLETE',
  CREATE_VERSION = 'CREATE_VERSION',
  VIEW = 'VIEW',
  DELETE = 'DELETE',
  MODIFY = 'MODIFY',
}

// 文件类型枚举
export enum FileType {
  CAD_DWG = 'CAD_DWG',
  CAD_DXF = 'CAD_DXF',
  PDF = 'PDF',
  JPEG = 'JPEG',
  PNG = 'PNG',
  WORD = 'WORD',
  EXCEL = 'EXCEL',
  OTHER = 'OTHER',
}

// 文档实体
export interface Document {
  id: number
  fileNumber: string
  fileName: string
  originalName: string
  productModel?: string
  version: string
  compileDate: string
  compilerId: number
  compilerName: string
  description?: string
  fileType: FileType
  fileSize: number
  filePath: string
  thumbnailPath?: string
  contentType?: string
  status: DocumentStatus
  isCurrentVersion: boolean
  createTime: string
  updateTime: string
}

// 审批记录
export interface ApprovalRecord {
  id: number
  documentId: number
  approvalFlowId: number
  stepOrder: number
  approverId: number
  approverName: string
  approverRoleId: number
  approverRoleName: string
  status: ApprovalStatus
  comment?: string
  approveTime?: string
  createTime: string
}

// 用户实体
export interface User {
  id: number
  username: string
  realName: string
  departmentId?: number
  departmentName?: string
  position?: string
  phone?: string
  email?: string
  enabled: boolean
  createTime: string
  updateTime: string
}

// 角色实体
export interface Role {
  id: number
  roleCode: string
  roleName: string
  description?: string
  enabled: boolean
  createTime: string
  updateTime: string
}

// 权限实体
export interface Permission {
  id: number
  permissionCode: string
  permissionName: string
  description?: string
  permissionType: string
  parentId?: number
  createTime: string
}

// 用户组实体
export interface UserGroup {
  id: number
  groupCode: string
  groupName: string
  description?: string
  enabled: boolean
  createTime: string
  updateTime: string
}

// 操作日志
export interface OperationLog {
  id: number
  userId: number
  userName: string
  operationType: OperationType
  operationContent?: string
  objectType?: string
  objectId?: number
  result?: string
  errorMessage?: string
  ipAddress?: string
  userAgent?: string
  createTime: string
}

// 文件下发记录
export interface DocumentDistribution {
  id: number
  documentId: number
  distributorId: number
  distributorName: string
  distributionNote?: string
  effectiveDate?: string
  targetType: string
  targetIds: string
  targetNames: string
  distributeTime: string
}

