import http from './http'
import type { ApiResponse } from './http'

export interface DistributionRequest {
  documentIds: number[]
  targetType: 'DEPARTMENT' | 'POSITION' | 'USER_GROUP' | 'USER'
  targetIds: number[]
  targetNames: string[]
  distributionNote?: string
  effectiveDate?: string
}

export const distributionApi = {
  // 下发文件
  distribute: (data: DistributionRequest) => {
    return http.post<ApiResponse<void>>('/v1/distributions/distribute', data)
  },

  // 回收文件
  recall: (documentIds: number[]) => {
    return http.post<ApiResponse<void>>('/v1/distributions/recall', documentIds)
  },

  // 作废文件
  obsolete: (documentIds: number[]) => {
    return http.post<ApiResponse<void>>('/v1/distributions/obsolete', documentIds)
  },

  // 记录文件查看
  recordView: (distributionId: number) => {
    return http.post<ApiResponse<void>>(`/v1/distributions/view/${distributionId}`)
  },

  // 记录文件下载
  recordDownload: (distributionId: number) => {
    return http.post<ApiResponse<void>>(`/v1/distributions/download/${distributionId}`)
  },
}

