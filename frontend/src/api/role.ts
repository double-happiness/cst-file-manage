import http from './http'
import type { ApiResponse } from './http'
import type { Role, Permission } from './types'

export interface AssignPermissionsRequest {
  permissionIds: number[]
}

export interface AssignRolesRequest {
  userId: number
  roleIds: number[]
}

export const roleApi = {
  // 创建角色
  create: (data: Role) => {
    return http.post<ApiResponse<Role>>('/v1/roles', data)
  },

  // 更新角色
  update: (id: number, data: Partial<Role>) => {
    return http.put<ApiResponse<Role>>(`/v1/roles/${id}`, data)
  },

  // 删除角色
  delete: (id: number) => {
    return http.delete<ApiResponse<void>>(`/v1/roles/${id}`)
  },

  // 获取所有角色
  getAll: () => {
    return http.get<ApiResponse<Role[]>>('/v1/roles')
  },

  // 获取角色详情
  getById: (id: number) => {
    return http.get<ApiResponse<Role>>(`/v1/roles/${id}`)
  },

  // 为角色分配权限
  assignPermissions: (id: number, data: AssignPermissionsRequest) => {
    return http.post<ApiResponse<void>>(`/v1/roles/${id}/permissions`, data)
  },

  // 获取角色的权限
  getPermissions: (id: number) => {
    return http.get<ApiResponse<Permission[]>>(`/v1/roles/${id}/permissions`)
  },

  // 为用户分配角色
  assignRolesToUser: (data: AssignRolesRequest) => {
    return http.post<ApiResponse<void>>('/v1/roles/assign', data)
  },

  // 获取用户的角色
  getUserRoles: (userId: number) => {
    return http.get<ApiResponse<Role[]>>(`/v1/roles/user/${userId}`)
  },
}

