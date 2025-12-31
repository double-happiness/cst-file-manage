import { useState, useEffect } from 'react'
import { Table, Button, Input, Select, DatePicker, Space, Tag, message } from 'antd'
import { SearchOutlined, DownloadOutlined } from '@ant-design/icons'
import { logApi } from '../../api/log'
import { userApi } from '../../api/user'
import type { OperationLog, OperationType, User } from '../../api/types'
import { OperationType as OpType } from '../../api/types'
import dayjs from 'dayjs'

const operationTypeMap: Record<OperationType, { color: string; text: string }> = {
  LOGIN: { color: 'blue', text: '登录' },
  LOGOUT: { color: 'default', text: '登出' },
  UPLOAD: { color: 'green', text: '上传文件' },
  DOWNLOAD: { color: 'cyan', text: '下载文件' },
  APPROVE: { color: 'success', text: '审批文件' },
  REJECT: { color: 'error', text: '驳回文件' },
  DISTRIBUTE: { color: 'purple', text: '下发文件' },
  RECALL: { color: 'warning', text: '回收文件' },
  OBSOLETE: { color: 'default', text: '作废文件' },
  CREATE_VERSION: { color: 'geekblue', text: '创建版本' },
  VIEW: { color: 'lime', text: '查看文件' },
  DELETE: { color: 'red', text: '删除文件' },
  MODIFY: { color: 'orange', text: '修改文件' },
}

export default function LogListPage() {
  const [logs, setLogs] = useState<OperationLog[]>([])
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(false)
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(20)
  const [searchParams, setSearchParams] = useState({
    userId: undefined as number | undefined,
    operationType: undefined as OperationType | undefined,
    startTime: undefined as string | undefined,
    endTime: undefined as string | undefined,
  })

  useEffect(() => {
    loadUsers()
    loadLogs()
  }, [page, pageSize])

  const loadUsers = async () => {
    try {
      const res = await userApi.getAll()
      // 后端返回格式: ApiResponse<List<User>>，res.data 就是 List<User>
      const userList = Array.isArray(res.data) ? res.data : []
      setUsers(userList)
    } catch (error) {
      console.error('加载用户列表失败:', error)
      setUsers([])
    }
  }

  const loadLogs = async () => {
    setLoading(true)
    try {
      const res = await logApi.search({
        ...searchParams,
        page: page - 1,
        size: pageSize,
      })
      const responseData = res.data
      if (responseData && typeof responseData === 'object' && 'content' in responseData) {
        setLogs(responseData.content || [])
        setTotal(responseData.totalElements || 0)
      } else if (Array.isArray(responseData)) {
        setLogs(responseData)
        setTotal(responseData.length)
      } else {
        setLogs([])
        setTotal(0)
      }
    } catch (error) {
      console.error('加载日志列表失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = () => {
    setPage(1)
    loadLogs()
  }

  const handleExport = async () => {
    try {
      await logApi.export(searchParams)
      message.success('导出成功')
    } catch (error) {
      console.error('导出日志失败:', error)
    }
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '操作人',
      dataIndex: 'userName',
      key: 'userName',
    },
    {
      title: '操作类型',
      dataIndex: 'operationType',
      key: 'operationType',
      render: (type: OperationType) => {
        const info = operationTypeMap[type]
        return <Tag color={info.color}>{info.text}</Tag>
      },
    },
    {
      title: '操作内容',
      dataIndex: 'operationContent',
      key: 'operationContent',
      ellipsis: true,
    },
    {
      title: '操作对象',
      dataIndex: 'objectType',
      key: 'objectType',
      render: (_: any, record: OperationLog) =>
        record.objectType ? `${record.objectType}(${record.objectId})` : '-',
    },
    {
      title: '操作结果',
      dataIndex: 'result',
      key: 'result',
      render: (result: string) => (
        <Tag color={result === 'SUCCESS' ? 'success' : 'error'}>{result || '-'}</Tag>
      ),
    },
    {
      title: 'IP地址',
      dataIndex: 'ipAddress',
      key: 'ipAddress',
    },
    {
      title: '操作时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (time: string) => new Date(time).toLocaleString('zh-CN'),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Space>
          <Select
            placeholder="操作人"
            value={searchParams.userId}
            onChange={(value) => setSearchParams({ ...searchParams, userId: value })}
            style={{ width: 150 }}
            allowClear
            showSearch
          >
            {(users || []).map((u) => (
              <Select.Option key={u.id} value={u.id}>
                {u.realName}
              </Select.Option>
            ))}
          </Select>
          <Select
            placeholder="操作类型"
            value={searchParams.operationType}
            onChange={(value) => setSearchParams({ ...searchParams, operationType: value })}
            style={{ width: 150 }}
            allowClear
          >
            {Object.entries(operationTypeMap).map(([key, { text }]) => (
              <Select.Option key={key} value={key}>
                {text}
              </Select.Option>
            ))}
          </Select>
          <DatePicker.RangePicker
            onChange={(dates) => {
              if (dates) {
                setSearchParams({
                  ...searchParams,
                  startTime: dates[0]?.format('YYYY-MM-DD HH:mm:ss'),
                  endTime: dates[1]?.format('YYYY-MM-DD HH:mm:ss'),
                })
              } else {
                setSearchParams({
                  ...searchParams,
                  startTime: undefined,
                  endTime: undefined,
                })
              }
            }}
          />
          <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
            搜索
          </Button>
          <Button icon={<DownloadOutlined />} onClick={handleExport}>
            导出Excel
          </Button>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={logs}
        rowKey="id"
        loading={loading}
        pagination={{
          current: page,
          pageSize: pageSize,
          total: total,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, pageSize) => {
            setPage(page)
            setPageSize(pageSize)
          },
        }}
      />
    </div>
  )
}

