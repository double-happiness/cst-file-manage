import http from './http'
import type { ApiResponse } from './http'
import type { UserGroup, User } from './types'

export interface AddMembersRequest {
  userIds: number[]
}

export interface RemoveMembersRequest {
  userIds: number[]
}

export const userGroupApi = {
  // 创建用户组
  create: (data: UserGroup) => {
    return http.post<ApiResponse<UserGroup>>('/v1/user-groups', data)
  },

  // 更新用户组
  update: (id: number, data: Partial<UserGroup>) => {
    return http.put<ApiResponse<UserGroup>>(`/v1/user-groups/${id}`, data)
  },

  // 删除用户组
  delete: (id: number) => {
    return http.delete<ApiResponse<void>>(`/v1/user-groups/${id}`)
  },

  // 获取所有用户组
  getAll: () => {
    return http.get<ApiResponse<UserGroup[]>>('/v1/user-groups')
  },

  // 获取用户组详情
  getById: (id: number) => {
    return http.get<ApiResponse<UserGroup>>(`/v1/user-groups/${id}`)
  },

  // 添加用户到用户组
  addMembers: (id: number, data: AddMembersRequest) => {
    return http.post<ApiResponse<void>>(`/v1/user-groups/${id}/members`, data)
  },

  // 从用户组移除用户
  removeMembers: (id: number, data: RemoveMembersRequest) => {
    return http.delete<ApiResponse<void>>(`/v1/user-groups/${id}/members`, { data })
  },

  // 获取用户组的所有成员
  getMembers: (id: number) => {
    return http.get<ApiResponse<User[]>>(`/v1/user-groups/${id}/members`)
  },

  // 获取用户所属的所有用户组
  getUserGroups: (userId: number) => {
    return http.get<ApiResponse<UserGroup[]>>(`/v1/user-groups/user/${userId}`)
  },
}

