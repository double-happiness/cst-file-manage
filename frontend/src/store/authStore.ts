import { create } from 'zustand'
import { persist, createJSONStorage } from 'zustand/middleware'

interface AuthState {
  token: string | null
  userId: number | null
  username: string | null
  realName: string | null
  setAuth: (payload: { token: string; userId: number; username: string; realName: string }) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      userId: null,
      username: null,
      realName: null,
      setAuth: ({ token, userId, username, realName }) =>
        set({ token, userId, username, realName }),
      logout: () => set({ token: null, userId: null, username: null, realName: null }),
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => localStorage),
    }
  )
)

