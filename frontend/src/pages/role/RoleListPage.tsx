import { useState, useEffect } from 'react'
import { Table, Button, Modal, Form, Input, Select, Space, message, Tag, Checkbox } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, SafetyOutlined } from '@ant-design/icons'
import { roleApi } from '../../api/role'
import { permissionApi } from '../../api/permission'
import type { Role, Permission } from '../../api/types'

export default function RoleListPage() {
  const [roles, setRoles] = useState<Role[]>([])
  const [permissions, setPermissions] = useState<Permission[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [permissionModalVisible, setPermissionModalVisible] = useState(false)
  const [currentRole, setCurrentRole] = useState<Role | null>(null)
  const [form] = Form.useForm()
  const [permissionForm] = Form.useForm()

  useEffect(() => {
    loadRoles()
    loadPermissions()
  }, [])

  const loadRoles = async () => {
    setLoading(true)
    try {
      const res = await roleApi.getAll()
      setRoles(res.data.data)
    } catch (error) {
      console.error('加载角色列表失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const loadPermissions = async () => {
    try {
      const res = await permissionApi.getAll()
      setPermissions(res.data.data)
    } catch (error) {
      console.error('加载权限列表失败:', error)
    }
  }

  const handleCreate = () => {
    setCurrentRole(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (role: Role) => {
    setCurrentRole(role)
    form.setFieldsValue(role)
    setModalVisible(true)
  }

  const handleDelete = (role: Role) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除角色"${role.roleName}"吗？`,
      onOk: async () => {
        try {
          await roleApi.delete(role.id)
          message.success('删除成功')
          loadRoles()
        } catch (error) {
          console.error('删除角色失败:', error)
        }
      },
    })
  }

  const handleAssignPermissions = async (role: Role) => {
    setCurrentRole(role)
    try {
      const res = await roleApi.getPermissions(role.id)
      permissionForm.setFieldsValue({ permissionIds: res.data.data.map((p) => p.id) })
      setPermissionModalVisible(true)
    } catch (error) {
      console.error('加载角色权限失败:', error)
    }
  }

  const handleSubmit = async (values: any) => {
    try {
      if (currentRole) {
        await roleApi.update(currentRole.id, values)
        message.success('更新成功')
      } else {
        await roleApi.create(values)
        message.success('创建成功')
      }
      setModalVisible(false)
      loadRoles()
    } catch (error) {
      console.error('保存角色失败:', error)
    }
  }

  const handlePermissionSubmit = async (values: { permissionIds: number[] }) => {
    if (!currentRole) return

    try {
      await roleApi.assignPermissions(currentRole.id, values)
      message.success('分配权限成功')
      setPermissionModalVisible(false)
    } catch (error) {
      console.error('分配权限失败:', error)
    }
  }

  const columns = [
    {
      title: '角色代码',
      dataIndex: 'roleCode',
      key: 'roleCode',
    },
    {
      title: '角色名称',
      dataIndex: 'roleName',
      key: 'roleName',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
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
      render: (_: any, record: Role) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button
            type="link"
            icon={<SafetyOutlined />}
            onClick={() => handleAssignPermissions(record)}
          >
            分配权限
          </Button>
          <Button type="link" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record)}>
            删除
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
          新增角色
        </Button>
      </div>
      <Table columns={columns} dataSource={roles} rowKey="id" loading={loading} />

      <Modal
        title={currentRole ? '编辑角色' : '新增角色'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={() => form.submit()}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="roleCode" label="角色代码" rules={[{ required: true }]}>
            <Input disabled={!!currentRole} />
          </Form.Item>
          <Form.Item name="roleName" label="角色名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea />
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
        title="分配权限"
        open={permissionModalVisible}
        onCancel={() => setPermissionModalVisible(false)}
        onOk={() => permissionForm.submit()}
        width={600}
      >
        <Form form={permissionForm} layout="vertical" onFinish={handlePermissionSubmit}>
          <Form.Item name="permissionIds" label="选择权限">
            <Checkbox.Group style={{ width: '100%' }}>
              {permissions.map((permission) => (
                <div key={permission.id} style={{ marginBottom: 8 }}>
                  <Checkbox value={permission.id}>{permission.permissionName}</Checkbox>
                </div>
              ))}
            </Checkbox.Group>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

