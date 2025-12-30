import { useState } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { Layout, Menu, Avatar, Dropdown, Space, message } from 'antd'
import {
  FileTextOutlined,
  UploadOutlined,
  CheckCircleOutlined,
  HistoryOutlined,
  SendOutlined,
  BranchesOutlined,
  UserOutlined,
  TeamOutlined,
  SafetyOutlined,
  UnlockOutlined,
  FileSearchOutlined,
  LogoutOutlined,
} from '@ant-design/icons'
import { useAuthStore } from '../store/authStore'
import type { MenuProps } from 'antd'

const { Header, Sider, Content } = Layout

const menuItems: MenuProps['items'] = [
  {
    key: '/documents',
    icon: <FileTextOutlined />,
    label: '文档管理',
    children: [
      { key: '/documents', label: '文档列表' },
      { key: '/documents/upload', label: '上传文档' },
    ],
  },
  {
    key: '/approvals',
    icon: <CheckCircleOutlined />,
    label: '审批管理',
    children: [
      { key: '/approvals/todo', label: '待审批' },
      { key: '/approvals/history', label: '审批历史' },
    ],
  },
  {
    key: '/distributions',
    icon: <SendOutlined />,
    label: '文件下发',
  },
  {
    key: '/versions',
    icon: <BranchesOutlined />,
    label: '版本管理',
  },
  {
    key: '/users',
    icon: <UserOutlined />,
    label: '用户管理',
  },
  {
    key: '/roles',
    icon: <SafetyOutlined />,
    label: '角色管理',
  },
  {
    key: '/permissions',
    icon: <UnlockOutlined />,
    label: '权限管理',
  },
  {
    key: '/user-groups',
    icon: <TeamOutlined />,
    label: '用户组管理',
  },
  {
    key: '/logs',
    icon: <FileSearchOutlined />,
    label: '操作日志',
  },
]

export default function MainLayout() {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const { realName, logout } = useAuthStore()

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key)
  }

  const handleLogout = () => {
    logout()
    message.success('已退出登录')
    navigate('/login')
  }

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ]

  // 获取当前选中的菜单项
  const getSelectedKeys = () => {
    const path = location.pathname
    if (path.startsWith('/documents')) {
      return [path]
    }
    if (path.startsWith('/approvals')) {
      return [path]
    }
    return [path]
  }

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
        <div
          style={{
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontSize: 18,
            fontWeight: 'bold',
          }}
        >
          {collapsed ? '文档' : '文档管理系统'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={getSelectedKeys()}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            background: '#fff',
            padding: '0 24px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <div style={{ fontSize: 20, fontWeight: 'bold' }}>文档管理系统</div>
          <Space>
            <span>欢迎，{realName}</span>
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Avatar style={{ cursor: 'pointer' }} icon={<UserOutlined />} />
            </Dropdown>
          </Space>
        </Header>
        <Content style={{ margin: '24px', background: '#fff', padding: '24px' }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

