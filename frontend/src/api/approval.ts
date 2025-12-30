import http from './http'
import type { ApiResponse } from './http'
import type { ApprovalRecord, ApprovalStatus } from './types'

export interface ApprovalRequest {
  status: ApprovalStatus
  comment?: string
}

export const approvalApi = {
  // 提交审批
  submit: (documentId: number) => {
    return http.post<ApiResponse<void>>(`/v1/approvals/submit/${documentId}`)
  },

  // 审批文档
  approve: (documentId: number, data: ApprovalRequest) => {
    return http.post<ApiResponse<void>>(`/v1/approvals/approve/${documentId}`, data)
  },

  // 查询审批进度
  getProgress: (documentId: number) => {
    return http.get<ApiResponse<ApprovalRecord[]>>(`/v1/approvals/progress/${documentId}`)
  },
}

