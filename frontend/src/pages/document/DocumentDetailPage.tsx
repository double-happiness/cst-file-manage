import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Card, Descriptions, Tag, Button, Space, message, Modal } from 'antd'
import { ArrowLeftOutlined, CheckCircleOutlined } from '@ant-design/icons'
import { documentApi } from '../../api/document'
import { approvalApi } from '../../api/approval'
import type { Document } from '../../api/types'
import { DocumentStatus } from '../../api/types'

const statusMap: Record<DocumentStatus, { color: string; text: string }> = {
  DRAFT: { color: 'default', text: '草稿' },
  PENDING_APPROVAL: { color: 'processing', text: '待审批' },
  APPROVING: { color: 'processing', text: '审批中' },
  APPROVED: { color: 'success', text: '已批准' },
  REJECTED: { color: 'error', text: '已驳回' },
  RECALLED: { color: 'warning', text: '已回收' },
  OBSOLETE: { color: 'default', text: '已作废' },
}

export default function DocumentDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [document, setDocument] = useState<Document | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (id) {
      loadDocument()
    }
  }, [id])

  const loadDocument = async () => {
    setLoading(true)
    try {
      const res = await documentApi.getById(Number(id))
      setDocument(res.data)
    } catch (error) {
      console.error('加载文档详情失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmitApproval = () => {
    Modal.confirm({
      title: '确认提交审批',
      content: '确定要提交此文档进行审批吗？',
      onOk: async () => {
        try {
          await approvalApi.submit(Number(id))
          message.success('提交审批成功')
          loadDocument()
        } catch (error) {
          console.error('提交审批失败:', error)
        }
      },
    })
  }

  if (!document) {
    return <div>加载中...</div>
  }

  return (
    <div>
      <Card
        title={
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/documents')}>
              返回
            </Button>
            <span>文档详情</span>
          </Space>
        }
        extra={
          document.status === DocumentStatus.DRAFT && (
            <Button type="primary" icon={<CheckCircleOutlined />} onClick={handleSubmitApproval}>
              提交审批
            </Button>
          )
        }
        loading={loading}
      >
        <Descriptions column={2} bordered>
          <Descriptions.Item label="文件编号">{document.fileNumber}</Descriptions.Item>
          <Descriptions.Item label="文件名称">{document.fileName}</Descriptions.Item>
          <Descriptions.Item label="原始文件名">{document.originalName}</Descriptions.Item>
          <Descriptions.Item label="产品型号">{document.productModel || '-'}</Descriptions.Item>
          <Descriptions.Item label="版本号">{document.version}</Descriptions.Item>
          <Descriptions.Item label="状态">
            <Tag color={statusMap[document.status].color}>{statusMap[document.status].text}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="编制日期">
            {new Date(document.compileDate).toLocaleString('zh-CN')}
          </Descriptions.Item>
          <Descriptions.Item label="编制人">{document.compilerName}</Descriptions.Item>
          <Descriptions.Item label="文件大小">
            {(document.fileSize / 1024 / 1024).toFixed(2)} MB
          </Descriptions.Item>
          <Descriptions.Item label="文件类型">{document.fileType}</Descriptions.Item>
          <Descriptions.Item label="是否当前版本">
            {document.isCurrentVersion ? '是' : '否'}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间" span={2}>
            {new Date(document.createTime).toLocaleString('zh-CN')}
          </Descriptions.Item>
          <Descriptions.Item label="文件描述" span={2}>
            {document.description || '-'}
          </Descriptions.Item>
        </Descriptions>
        <div style={{ marginTop: 16 }}>
          <Button
            type="primary"
            onClick={() => window.open(`/api/v1/preview/${document.id}`, '_blank')}
          >
            预览文件
          </Button>
        </div>
      </Card>
    </div>
  )
}

