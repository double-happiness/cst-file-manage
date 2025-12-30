import http from './http'
import type { ApiResponse } from './http'
import type { User } from './types'

export interface ResetPasswordRequest {
  newPassword: string
}

export const userApi = {
  // 创建用户
  create: (data: User) => {
    return http.post<ApiResponse<User>>('/v1/users', data)
  },

  // 更新用户
  update: (id: number, data: Partial<User>) => {
    return http.put<ApiResponse<User>>(`/v1/users/${id}`, data)
  },

  // 重置密码
  resetPassword: (id: number, data: ResetPasswordRequest) => {
    return http.post<ApiResponse<void>>(`/v1/users/${id}/reset-password`, data)
  },

  // 获取所有用户
  getAll: () => {
    return http.get<ApiResponse<User[]>>('/v1/users')
  },

  // 获取用户详情
  getById: (id: number) => {
    return http.get<ApiResponse<User>>(`/v1/users/${id}`)
  },
}

