import { createBrowserRouter, Navigate } from 'react-router-dom'
import LoginPage from '../pages/auth/LoginPage'
import MainLayout from '../layout/MainLayout'
import DocumentListPage from '../pages/document/DocumentListPage'
import DocumentUploadPage from '../pages/document/DocumentUploadPage'
import DocumentDetailPage from '../pages/document/DocumentDetailPage'
import ApprovalTodoPage from '../pages/approval/ApprovalTodoPage'
import ApprovalHistoryPage from '../pages/approval/ApprovalHistoryPage'
import DistributionPage from '../pages/distribution/DistributionPage'
import VersionListPage from '../pages/version/VersionListPage'
import VersionComparePage from '../pages/version/VersionComparePage'
import UserListPage from '../pages/user/UserListPage'
import RoleListPage from '../pages/role/RoleListPage'
import PermissionListPage from '../pages/permission/PermissionListPage'
import UserGroupPage from '../pages/userGroup/UserGroupPage'
import LogListPage from '../pages/log/LogListPage'

import { useAuthStore } from '../store/authStore'
import NotFoundPage from "@/pages/err/NotFoundPage.tsx";

// 路由守卫组件
function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const token = useAuthStore.getState().token
  return token ? <>{children}</> : <Navigate to="/login" replace />
}

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <MainLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/documents" replace />,
      },
      {
        path: 'documents',
        element: <DocumentListPage />,
      },
      {
        path: 'documents/upload',
        element: <DocumentUploadPage />,
      },
      {
        path: 'documents/:id',
        element: <DocumentDetailPage />,
      },
      {
        path: 'approvals/todo',
        element: <ApprovalTodoPage />,
      },
      {
        path: 'approvals/history',
        element: <ApprovalHistoryPage />,
      },
      {
        path: 'distributions',
        element: <DistributionPage />,
      },
      {
        path: 'versions/:fileNumber',
        element: <VersionListPage />,
      },
      {
        path: 'versions/compare',
        element: <VersionComparePage />,
      },
      {
        path: 'users',
        element: <UserListPage />,
      },
      {
        path: 'roles',
        element: <RoleListPage />,
      },
      {
        path: 'permissions',
        element: <PermissionListPage />,
      },
      {
        path: 'user-groups',
        element: <UserGroupPage />,
      },
      {
        path: 'logs',
        element: <LogListPage />,
      },
      {
        path: '*',
        element: <NotFoundPage />,
      },
    ],
  },
])

