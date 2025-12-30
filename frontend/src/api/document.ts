import http from './http'
import type { ApiResponse } from './http'
import type { Document, DocumentStatus } from './types'

export interface DocumentSearchParams {
  fileNumber?: string
  fileName?: string
  productModel?: string
  status?: DocumentStatus
  compilerId?: number
  page?: number
  size?: number
}

export interface DocumentUploadParams {
  file: File
  fileNumber: string
  fileName: string
  productModel?: string
  version: string
  compileDate: string
  description?: string
}

export const documentApi = {
  // 上传文档
  upload: (data: DocumentUploadParams) => {
    const formData = new FormData()
    formData.append('file', data.file)
    formData.append('fileNumber', data.fileNumber)
    formData.append('fileName', data.fileName)
    if (data.productModel) formData.append('productModel', data.productModel)
    formData.append('version', data.version)
    formData.append('compileDate', data.compileDate)
    if (data.description) formData.append('description', data.description)

    return http.post<ApiResponse<Document>>('/v1/documents/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },

  // 获取文档详情
  getById: (id: number) => {
    return http.get<ApiResponse<Document>>(`/v1/documents/${id}`)
  },

  // 搜索文档
  search: (data: DocumentSearchParams) => {
    const res = http.post<ApiResponse<Document>>('/v1/documents/search', data)
    console.log("res--------", res)
    return res
  }
}
