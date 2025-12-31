import { useState, useEffect } from 'react'
import { Table, Button, Input, Select, Space, Tag, message } from 'antd'
import { SearchOutlined, PlusOutlined, EyeOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { documentApi } from '../../api/document'
import type { Document, DocumentStatus } from '../../api/types'

const statusMap: Record<DocumentStatus, { color: string; text: string }> = {
  DRAFT: { color: 'default', text: '草稿' },
  PENDING_APPROVAL: { color: 'processing', text: '待审批' },
  APPROVING: { color: 'processing', text: '审批中' },
  APPROVED: { color: 'success', text: '已批准' },
  REJECTED: { color: 'error', text: '已驳回' },
  RECALLED: { color: 'warning', text: '已回收' },
  OBSOLETE: { color: 'default', text: '已作废' },
}

export default function DocumentListPage() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [documents, setDocuments] = useState<Document[]>([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [searchParams, setSearchParams] = useState({
    fileNumber: '',
    fileName: '',
    productModel: '',
    status: undefined as DocumentStatus | undefined,
  })

  const loadDocuments = async () => {
    setLoading(true)
    try {
      const res = await documentApi.search({
        ...searchParams,
        page: page - 1,
        size: pageSize,
      })
      const responseData = res.data
      if (responseData && typeof responseData === 'object' && 'content' in responseData) {
        setDocuments(responseData.content || [])
        setTotal(responseData.totalElements || 0)
      } else if (Array.isArray(responseData.content)) {
        // Handle case where API returns array directly
        setDocuments(responseData)
        setTotal(responseData.length)
      } else {
        setDocuments([])
        setTotal(0)
      }
    } catch (error) {
      console.error('加载文档列表失败:', error)
      setDocuments([])
      setTotal(0)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadDocuments()
  }, [page, pageSize])

  const handleSearch = () => {
    setPage(1)
    loadDocuments()
  }

  const columns = [
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
      title: '产品型号',
      dataIndex: 'productModel',
      key: 'productModel',
    },
    {
      title: '版本号',
      dataIndex: 'version',
      key: 'version',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: DocumentStatus) => {
        const statusInfo = statusMap[status]
        return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>
      },
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
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => navigate(`/documents/${record.id}`)}
          >
            查看
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Space>
          <Input
            placeholder="文件编号"
            value={searchParams.fileNumber}
            onChange={(e) => setSearchParams({ ...searchParams, fileNumber: e.target.value })}
            style={{ width: 200 }}
          />
          <Input
            placeholder="文件名称"
            value={searchParams.fileName}
            onChange={(e) => setSearchParams({ ...searchParams, fileName: e.target.value })}
            style={{ width: 200 }}
          />
          <Input
            placeholder="产品型号"
            value={searchParams.productModel}
            onChange={(e) => setSearchParams({ ...searchParams, productModel: e.target.value })}
            style={{ width: 200 }}
          />
          <Select
            placeholder="状态"
            value={searchParams.status}
            onChange={(value) => setSearchParams({ ...searchParams, status: value })}
            style={{ width: 150 }}
            allowClear
          >
            {Object.entries(statusMap).map(([key, { text }]) => (
              <Select.Option key={key} value={key}>
                {text}
              </Select.Option>
            ))}
          </Select>
          <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
            搜索
          </Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/documents/upload')}>
            上传文档
          </Button>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={documents}
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
