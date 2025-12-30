import { useState, useEffect } from 'react'
import { Table, Button, Modal, Form, Input, Space, message, Tag, Transfer } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, TeamOutlined } from '@ant-design/icons'
import { userGroupApi } from '../../api/userGroup'
import { userApi } from '../../api/user'
import type { UserGroup, User } from '../../api/types'

export default function UserGroupPage() {
  const [userGroups, setUserGroups] = useState<UserGroup[]>([])
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [memberModalVisible, setMemberModalVisible] = useState(false)
  const [currentGroup, setCurrentGroup] = useState<UserGroup | null>(null)
  const [targetKeys, setTargetKeys] = useState<string[]>([])
  const [form] = Form.useForm()

  useEffect(() => {
    loadUserGroups()
    loadUsers()
  }, [])

  const loadUserGroups = async () => {
    setLoading(true)
    try {
      const res = await userGroupApi.getAll()
      setUserGroups(res.data.data)
    } catch (error) {
      console.error('加载用户组列表失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const loadUsers = async () => {
    try {
      const res = await userApi.getAll()
      setUsers(res.data.data)
    } catch (error) {
      console.error('加载用户列表失败:', error)
    }
  }

  const handleCreate = () => {
    setCurrentGroup(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (group: UserGroup) => {
    setCurrentGroup(group)
    form.setFieldsValue(group)
    setModalVisible(true)
  }

  const handleDelete = (group: UserGroup) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除用户组"${group.groupName}"吗？`,
      onOk: async () => {
        try {
          await userGroupApi.delete(group.id)
          message.success('删除成功')
          loadUserGroups()
        } catch (error) {
          console.error('删除用户组失败:', error)
        }
      },
    })
  }

  const handleManageMembers = async (group: UserGroup) => {
    setCurrentGroup(group)
    try {
      const res = await userGroupApi.getMembers(group.id)
      setTargetKeys(res.data.data.map((u) => String(u.id)))
      setMemberModalVisible(true)
    } catch (error) {
      console.error('加载成员列表失败:', error)
    }
  }

  const handleSubmit = async (values: any) => {
    try {
      if (currentGroup) {
        await userGroupApi.update(currentGroup.id, values)
        message.success('更新成功')
      } else {
        await userGroupApi.create(values)
        message.success('创建成功')
      }
      setModalVisible(false)
      loadUserGroups()
    } catch (error) {
      console.error('保存用户组失败:', error)
    }
  }

  const handleMemberSubmit = async () => {
    if (!currentGroup) return

    try {
      const currentMembers = await userGroupApi.getMembers(currentGroup.id)
      const currentMemberIds = currentMembers.data.data.map((u) => u.id)
      const newMemberIds = targetKeys.map(Number)

      // 计算需要添加和移除的成员
      const toAdd = newMemberIds.filter((id) => !currentMemberIds.includes(id))
      const toRemove = currentMemberIds.filter((id) => !newMemberIds.includes(id))

      if (toAdd.length > 0) {
        await userGroupApi.addMembers(currentGroup.id, { userIds: toAdd })
      }
      if (toRemove.length > 0) {
        await userGroupApi.removeMembers(currentGroup.id, { userIds: toRemove })
      }

      message.success('成员管理成功')
      setMemberModalVisible(false)
    } catch (error) {
      console.error('管理成员失败:', error)
    }
  }

  const columns = [
    {
      title: '用户组代码',
      dataIndex: 'groupCode',
      key: 'groupCode',
    },
    {
      title: '用户组名称',
      dataIndex: 'groupName',
      key: 'groupName',
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
      render: (_: any, record: UserGroup) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button
            type="link"
            icon={<TeamOutlined />}
            onClick={() => handleManageMembers(record)}
          >
            管理成员
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
          新增用户组
        </Button>
      </div>
      <Table columns={columns} dataSource={userGroups} rowKey="id" loading={loading} />

      <Modal
        title={currentGroup ? '编辑用户组' : '新增用户组'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={() => form.submit()}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="groupCode" label="用户组代码" rules={[{ required: true }]}>
            <Input disabled={!!currentGroup} />
          </Form.Item>
          <Form.Item name="groupName" label="用户组名称" rules={[{ required: true }]}>
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
        title="管理成员"
        open={memberModalVisible}
        onCancel={() => setMemberModalVisible(false)}
        onOk={handleMemberSubmit}
        width={600}
      >
        <Transfer
          dataSource={users.map((u) => ({ key: String(u.id), title: u.realName }))}
          titles={['可选用户', '已选用户']}
          targetKeys={targetKeys}
          onChange={setTargetKeys}
          render={(item) => item.title}
          listStyle={{ width: 250, height: 300 }}
        />
      </Modal>
    </div>
  )
}

