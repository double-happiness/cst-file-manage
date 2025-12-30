import http from './http'

export const previewApi = {
  // 预览文件
  preview: (documentId: number) => {
    return `/api/v1/preview/${documentId}`
  },

  // PDF预览（返回第一页图片）
  previewPdf: (documentId: number) => {
    return `/api/v1/preview/pdf/${documentId}`
  },

  // 图片预览
  previewImage: (documentId: number) => {
    return `/api/v1/preview/image/${documentId}`
  },
}

