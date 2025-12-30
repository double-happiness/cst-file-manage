import http from './http'
import type { ApiResponse } from './http'
import type { Permission } from './types'

export const permissionApi = {
  // 创建权限
  create: (data: Permission) => {
    return http.post<ApiResponse<Permission>>('/v1/permissions', data)
  },

  // 更新权限
  update: (id: number, data: Partial<Permission>) => {
    return http.put<ApiResponse<Permission>>(`/v1/permissions/${id}`, data)
  },

  // 获取所有权限
  getAll: () => {
    return http.get<ApiResponse<Permission[]>>('/v1/permissions')
  },

  // 获取权限树
  getTree: () => {
    return http.get<ApiResponse<Permission[]>>('/v1/permissions/tree')
  },

  // 获取当前用户的权限
  getMyPermissions: () => {
    return http.get<ApiResponse<string[]>>('/v1/permissions/my')
  },
}

