import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Table, Button, Tag, Space, message, Modal } from 'antd'
import { ArrowLeftOutlined, BranchesOutlined, ReloadOutlined } from '@ant-design/icons'
import { versionApi } from '../../api/version'
import type { Document } from '../../api/types'

export default function VersionListPage() {
  const { fileNumber } = useParams<{ fileNumber: string }>()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [versions, setVersions] = useState<Document[]>([])

  useEffect(() => {
    if (fileNumber) {
      loadVersions()
    }
  }, [fileNumber])

  const loadVersions = async () => {
    if (!fileNumber) return
    setLoading(true)
    try {
      const res = await versionApi.getVersions(fileNumber)
      // 后端返回格式: ApiResponse<List<Document>>，res.data 就是 List<Document>
      const versionList = Array.isArray(res.data) ? res.data : []
      setVersions(versionList)
    } catch (error) {
      console.error('加载版本列表失败:', error)
      message.error('加载版本列表失败')
      setVersions([])
    } finally {
      setLoading(false)
    }
  }

  const handleRestore = (versionId: number) => {
    Modal.confirm({
      title: '确认恢复版本',
      content: '确定要恢复此版本为当前有效版本吗？恢复后需要重新审批。',
      onOk: async () => {
        try {
          await versionApi.restore(versionId)
          message.success('恢复版本成功')
          loadVersions()
        } catch (error) {
          console.error('恢复版本失败:', error)
        }
      },
    })
  }

  const columns = [
    {
      title: '版本号',
      dataIndex: 'version',
      key: 'version',
    },
    {
      title: '是否当前版本',
      dataIndex: 'isCurrentVersion',
      key: 'isCurrentVersion',
      render: (isCurrent: boolean) => (
        <Tag color={isCurrent ? 'success' : 'default'}>{isCurrent ? '是' : '否'}</Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => <Tag>{status}</Tag>,
    },
    {
      title: '编制日期',
      dataIndex: 'compileDate',
      key: 'compileDate',
      render: (date: string) => new Date(date).toLocaleString('zh-CN'),
    },
    {
      title: '编制人',
      dataIndex: 'compilerName',
      key: 'compilerName',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (time: string) => new Date(time).toLocaleString('zh-CN'),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Document) => (
        <Space>
          <Button type="link" onClick={() => navigate(`/documents/${record.id}`)}>
            查看
          </Button>
          {!record.isCurrentVersion && (
            <Button type="link" icon={<ReloadOutlined />} onClick={() => handleRestore(record.id)}>
              恢复版本
            </Button>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/documents')}>
            返回
          </Button>
          <span style={{ fontSize: 16, fontWeight: 'bold' }}>
            文件编号：{fileNumber} - 版本列表
          </span>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={versions}
        rowKey="id"
        loading={loading}
        pagination={false}
      />
    </div>
  )
}

