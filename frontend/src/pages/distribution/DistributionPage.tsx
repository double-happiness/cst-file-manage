import { useState, useEffect } from 'react'
import { Card, Table, Button, Select, Input, DatePicker, Form, message, Space, Transfer } from 'antd'
import { SendOutlined, UndoOutlined, DeleteOutlined } from '@ant-design/icons'
import { distributionApi } from '../../api/distribution'
import { documentApi } from '../../api/document'
import { userApi } from '../../api/user'
import { userGroupApi } from '../../api/userGroup'
import type { Document, User, UserGroup } from '../../api/types'
import dayjs from 'dayjs'

const { TextArea } = Input

export default function DistributionPage() {
  const [form] = Form.useForm()
  const [selectedDocuments, setSelectedDocuments] = useState<number[]>([])
  const [documents, setDocuments] = useState<Document[]>([])
  const [users, setUsers] = useState<User[]>([])
  const [userGroups, setUserGroups] = useState<UserGroup[]>([])
  const [targetType, setTargetType] = useState<'USER' | 'USER_GROUP' | 'DEPARTMENT' | 'POSITION'>('USER')
  const [targetKeys, setTargetKeys] = useState<string[]>([])
  const [targetNames, setTargetNames] = useState<string[]>([])

  useEffect(() => {
    loadApprovedDocuments()
    loadUsers()
    loadUserGroups()
  }, [])

  const loadApprovedDocuments = async () => {
    try {
      const res = await documentApi.search({
        status: 'APPROVED' as any,
        page: 0,
        size: 100,
      })
      // 后端返回格式可能是分页对象或数组，需要统一处理
      const responseData = res.data
      if (responseData && typeof responseData === 'object' && 'content' in responseData) {
        // 分页格式: { content: [], totalElements: 0, totalPages: 0 }
        setDocuments(Array.isArray(responseData.content) ? responseData.content : [])
      } else if (Array.isArray(responseData)) {
        // 直接数组格式
        setDocuments(responseData)
      } else {
        setDocuments([])
      }
    } catch (error) {
      console.error('加载文档失败:', error)
      message.error('加载文档失败')
      setDocuments([])
    }
  }

  const loadUsers = async () => {
    try {
      const res = await userApi.getAll()
      // 后端返回格式: ApiResponse<List<User>>，res.data 就是 List<User>
      const userList = Array.isArray(res.data) ? res.data : []
      setUsers(userList)
    } catch (error) {
      console.error('加载用户失败:', error)
      setUsers([])
    }
  }

  const loadUserGroups = async () => {
    try {
      const res = await userGroupApi.getAll()
      // 后端返回格式: ApiResponse<List<UserGroup>>，res.data 就是 List<UserGroup>
      const groupList = Array.isArray(res.data) ? res.data : []
      setUserGroups(groupList)
    } catch (error) {
      console.error('加载用户组失败:', error)
      setUserGroups([])
    }
  }

  const handleDistribute = async (values: any) => {
    if (selectedDocuments.length === 0) {
      message.warning('请选择要下发的文档')
      return
    }

    if (targetKeys.length === 0) {
      message.warning('请选择下发对象')
      return
    }

    try {
      await distributionApi.distribute({
        documentIds: selectedDocuments,
        targetType,
        targetIds: targetKeys.map(Number),
        targetNames,
        distributionNote: values.distributionNote,
        effectiveDate: values.effectiveDate?.format('YYYY-MM-DD HH:mm:ss'),
      })
      message.success('下发成功')
      form.resetFields()
      setSelectedDocuments([])
      setTargetKeys([])
      setTargetNames([])
    } catch (error) {
      console.error('下发失败:', error)
    }
  }

  const handleRecall = async () => {
    if (selectedDocuments.length === 0) {
      message.warning('请选择要回收的文档')
      return
    }

    try {
      await distributionApi.recall(selectedDocuments)
      message.success('回收成功')
      setSelectedDocuments([])
    } catch (error) {
      console.error('回收失败:', error)
    }
  }

  const handleObsolete = async () => {
    if (selectedDocuments.length === 0) {
      message.warning('请选择要作废的文档')
      return
    }

    try {
      await distributionApi.obsolete(selectedDocuments)
      message.success('作废成功')
      setSelectedDocuments([])
    } catch (error) {
      console.error('作废失败:', error)
    }
  }

  const getDataSource = () => {
    if (targetType === 'USER') {
      return (users || []).map((u) => ({ key: String(u.id), title: u.realName }))
    } else if (targetType === 'USER_GROUP') {
      return (userGroups || []).map((g) => ({ key: String(g.id), title: g.groupName }))
    }
    return []
  }

  const handleTransferChange = (keys: string[], direction: string, moveKeys: string[]) => {
    setTargetKeys(keys)
    const names = keys.map((key) => {
      if (targetType === 'USER') {
        const user = (users || []).find((u) => String(u.id) === key)
        return user?.realName || ''
      } else if (targetType === 'USER_GROUP') {
        const group = (userGroups || []).find((g) => String(g.id) === key)
        return group?.groupName || ''
      }
      return ''
    })
    setTargetNames(names)
  }

  const documentColumns = [
    {
      title: '文件编号',
      dataIndex: 'fileNumber',
      key: 'fileNumber',
    },
    {
      title: '文件名称',
      dataIndex: 'fileName',
      key: 'fileName',
    },
    {
      title: '版本号',
      dataIndex: 'version',
      key: 'version',
    },
  ]

  return (
    <div>
      <Card title="文件下发" style={{ marginBottom: 16 }}>
        <Form form={form} layout="vertical" onFinish={handleDistribute}>
          <Form.Item label="选择文档">
            <Table
              rowSelection={{
                selectedRowKeys: selectedDocuments,
                onChange: (keys) => setSelectedDocuments(keys as number[]),
              }}
              columns={documentColumns}
              dataSource={documents}
              rowKey="id"
              pagination={false}
              size="small"
            />
          </Form.Item>

          <Form.Item label="下发对象类型">
            <Select
              value={targetType}
              onChange={(value) => {
                setTargetType(value)
                setTargetKeys([])
                setTargetNames([])
              }}
            >
              <Select.Option value="USER">用户</Select.Option>
              <Select.Option value="USER_GROUP">用户组</Select.Option>
              <Select.Option value="DEPARTMENT">部门</Select.Option>
              <Select.Option value="POSITION">岗位</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item label="选择下发对象">
            <Transfer
              dataSource={getDataSource()}
              titles={['可选', '已选']}
              targetKeys={targetKeys}
              onChange={handleTransferChange}
              render={(item) => item.title}
              listStyle={{ width: 300, height: 300 }}
            />
          </Form.Item>

          <Form.Item name="distributionNote" label="下发说明">
            <TextArea rows={3} placeholder="请输入下发说明" />
          </Form.Item>

          <Form.Item name="effectiveDate" label="生效日期">
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SendOutlined />}>
                下发
              </Button>
              <Button icon={<UndoOutlined />} onClick={handleRecall}>
                回收选中
              </Button>
              <Button danger icon={<DeleteOutlined />} onClick={handleObsolete}>
                作废选中
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}

