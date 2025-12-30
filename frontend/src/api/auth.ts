import http from './http'
import type { ApiResponse } from './http'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  userId: number
  username: string
  realName: string
  expiresIn: number
}

export const authApi = {
  login: (data: LoginRequest) => {
    return http.post<ApiResponse<LoginResponse>>('/v1/auth/login', data)
  },
}

