import { useState, useEffect } from 'react'
import { Table, Button, Modal, Form, Input, Select, Space, message, Tag } from 'antd'
import { PlusOutlined, EditOutlined, KeyOutlined } from '@ant-design/icons'
import { userApi } from '../../api/user'
import type { User } from '../../api/types'

export default function UserListPage() {
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [resetPasswordModalVisible, setResetPasswordModalVisible] = useState(false)
  const [currentUser, setCurrentUser] = useState<User | null>(null)
  const [form] = Form.useForm()
  const [resetPasswordForm] = Form.useForm()

  useEffect(() => {
    loadUsers()
  }, [])

  const loadUsers = async () => {
    setLoading(true)
    try {
      const res = await userApi.getAll()
      // 后端返回格式: ApiResponse<List<User>>，res.data 就是 List<User>
      const userList = Array.isArray(res.data) ? res.data : []
      setUsers(userList)
      if (userList.length === 0) {
        console.warn('用户列表为空')
      }
    } catch (error) {
      console.error('加载用户列表失败:', error)
      message.error('加载用户列表失败')
      setUsers([])
    } finally {
      setLoading(false)
    }
  }

  const handleCreate = () => {
    setCurrentUser(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (user: User) => {
    setCurrentUser(user)
    form.setFieldsValue(user)
    setModalVisible(true)
  }

  const handleResetPassword = (user: User) => {
    setCurrentUser(user)
    resetPasswordForm.resetFields()
    setResetPasswordModalVisible(true)
  }

  const handleSubmit = async (values: any) => {
    try {
      if (currentUser) {
        await userApi.update(currentUser.id, values)
        message.success('更新成功')
      } else {
        await userApi.create(values)
        message.success('创建成功')
      }
      setModalVisible(false)
      loadUsers()
    } catch (error) {
      console.error('保存用户失败:', error)
    }
  }

  const handleResetPasswordSubmit = async (values: { newPassword: string }) => {
    if (!currentUser) return

    try {
      await userApi.resetPassword(currentUser.id, values)
      message.success('密码重置成功')
      setResetPasswordModalVisible(false)
    } catch (error) {
      console.error('重置密码失败:', error)
    }
  }

  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '姓名',
      dataIndex: 'realName',
      key: 'realName',
    },
    {
      title: '部门',
      dataIndex: 'departmentName',
      key: 'departmentName',
    },
    {
      title: '岗位',
      dataIndex: 'position',
      key: 'position',
    },
    {
      title: '联系方式',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      key: 'enabled',
      render: (enabled: boolean) => (
        <Tag color={enabled ? 'success' : 'error'}>{enabled ? '启用' : '禁用'}</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: User) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="link" icon={<KeyOutlined />} onClick={() => handleResetPassword(record)}>
            重置密码
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
          新增用户
        </Button>
      </div>
      <Table columns={columns} dataSource={users} rowKey="id" loading={loading} />

      <Modal
        title={currentUser ? '编辑用户' : '新增用户'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={() => form.submit()}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input disabled={!!currentUser} />
          </Form.Item>
          <Form.Item name="realName" label="姓名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: !currentUser }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="departmentId" label="部门ID">
            <Input type="number" />
          </Form.Item>
          <Form.Item name="departmentName" label="部门名称">
            <Input />
          </Form.Item>
          <Form.Item name="position" label="岗位">
            <Input />
          </Form.Item>
          <Form.Item name="phone" label="手机号">
            <Input />
          </Form.Item>
          <Form.Item name="email" label="邮箱">
            <Input />
          </Form.Item>
          <Form.Item name="enabled" label="状态" initialValue={true}>
            <Select>
              <Select.Option value={true}>启用</Select.Option>
              <Select.Option value={false}>禁用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="重置密码"
        open={resetPasswordModalVisible}
        onCancel={() => setResetPasswordModalVisible(false)}
        onOk={() => resetPasswordForm.submit()}
      >
        <Form form={resetPasswordForm} layout="vertical" onFinish={handleResetPasswordSubmit}>
          <Form.Item label="用户">{currentUser?.realName}</Form.Item>
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[{ required: true, message: '请输入新密码' }]}
          >
            <Input.Password />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

