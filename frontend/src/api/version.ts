import http from './http'
import type { ApiResponse } from './http'
import type { Document } from './types'

export interface CreateVersionRequest {
  newVersion: string
  changeDescription?: string
  changeReason?: string
  changeDate: string
}

export interface VersionComparisonResult {
  version1: Document
  version2: Document
  differences: string
  hasDifferences: boolean
}

export const versionApi = {
  // 创建新版本
  create: (documentId: number, data: CreateVersionRequest) => {
    return http.post<ApiResponse<Document>>(`/v1/versions/create/${documentId}`, data)
  },

  // 查询文档的所有版本
  getVersions: (fileNumber: string) => {
    return http.get<ApiResponse<Document[]>>(`/v1/versions/${fileNumber}`)
  },

  // 恢复历史版本
  restore: (versionId: number) => {
    return http.post<ApiResponse<void>>(`/v1/versions/restore/${versionId}`)
  },

  // 版本对比
  compare: (version1Id: number, version2Id: number) => {
    return http.get<ApiResponse<VersionComparisonResult>>('/v1/versions/compare', {
      params: { version1Id, version2Id },
    })
  },
}

