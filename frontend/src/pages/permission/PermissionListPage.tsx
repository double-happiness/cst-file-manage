import { useState, useEffect } from 'react'
import { Table, Button, Modal, Form, Input, Select, Space, message, Tree } from 'antd'
import { PlusOutlined, EditOutlined } from '@ant-design/icons'
import { permissionApi } from '../../api/permission'
import type { Permission } from '../../api/types'

export default function PermissionListPage() {
  const [permissions, setPermissions] = useState<Permission[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [currentPermission, setCurrentPermission] = useState<Permission | null>(null)
  const [form] = Form.useForm()

  useEffect(() => {
    loadPermissions()
  }, [])

  const loadPermissions = async () => {
    setLoading(true)
    try {
      const res = await permissionApi.getAll()
      setPermissions(res.data.data)
    } catch (error) {
      console.error('加载权限列表失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleCreate = () => {
    setCurrentPermission(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (permission: Permission) => {
    setCurrentPermission(permission)
    form.setFieldsValue(permission)
    setModalVisible(true)
  }

  const handleSubmit = async (values: any) => {
    try {
      if (currentPermission) {
        await permissionApi.update(currentPermission.id, values)
        message.success('更新成功')
      } else {
        await permissionApi.create(values)
        message.success('创建成功')
      }
      setModalVisible(false)
      loadPermissions()
    } catch (error) {
      console.error('保存权限失败:', error)
    }
  }

  const columns = [
    {
      title: '权限代码',
      dataIndex: 'permissionCode',
      key: 'permissionCode',
    },
    {
      title: '权限名称',
      dataIndex: 'permissionName',
      key: 'permissionName',
    },
    {
      title: '权限类型',
      dataIndex: 'permissionType',
      key: 'permissionType',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Permission) => (
        <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
          编辑
        </Button>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
          新增权限
        </Button>
      </div>
      <Table columns={columns} dataSource={permissions} rowKey="id" loading={loading} />

      <Modal
        title={currentPermission ? '编辑权限' : '新增权限'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={() => form.submit()}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="permissionCode" label="权限代码" rules={[{ required: true }]}>
            <Input disabled={!!currentPermission} />
          </Form.Item>
          <Form.Item name="permissionName" label="权限名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="permissionType" label="权限类型" rules={[{ required: true }]}>
            <Select>
              <Select.Option value="MODULE">功能模块</Select.Option>
              <Select.Option value="OPERATION">操作</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea />
          </Form.Item>
          <Form.Item name="parentId" label="父权限ID">
            <Input type="number" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

