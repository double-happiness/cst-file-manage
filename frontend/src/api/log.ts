import http from './http'
import type { ApiResponse } from './http'
import type { OperationLog, OperationType } from './types'
import { saveAs } from 'file-saver'

export interface LogSearchParams {
  userId?: number
  operationType?: OperationType
  startTime?: string
  endTime?: string
  page?: number
  size?: number
}

export const logApi = {
  // 查询日志
  search: (params: LogSearchParams) => {
    return http.get<ApiResponse<{ content: OperationLog[]; totalElements: number; totalPages: number }>>(
      '/v1/logs',
      { params }
    )
  },

  // 导出日志为Excel
  export: async (params: Omit<LogSearchParams, 'page' | 'size'>) => {
    const response = await http.get<Blob>('/v1/logs/export', {
      params,
      responseType: 'blob',
    })
    const blob = new Blob([response as any], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    })
    const filename = `操作日志_${new Date().toISOString().slice(0, 10)}.xlsx`
    saveAs(blob, filename)
  },
}

