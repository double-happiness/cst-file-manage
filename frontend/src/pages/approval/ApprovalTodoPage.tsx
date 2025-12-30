import { useState, useEffect } from 'react'
import { Table, Button, Modal, Form, Input, Select, Tag, message, Space } from 'antd'
import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { approvalApi } from '../../api/approval'
import { documentApi } from '../../api/document'
import type { Document, ApprovalRecord, ApprovalStatus } from '../../api/types'
import { ApprovalStatus as AppStatus } from '../../api/types'

const { TextArea } = Input

export default function ApprovalTodoPage() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [documents, setDocuments] = useState<Document[]>([])
  const [approvalModalVisible, setApprovalModalVisible] = useState(false)
  const [currentDocument, setCurrentDocument] = useState<Document | null>(null)
  const [approvalRecords, setApprovalRecords] = useState<ApprovalRecord[]>([])
  const [form] = Form.useForm()

  useEffect(() => {
    loadTodoDocuments()
  }, [])

  const loadTodoDocuments = async () => {
    // 这里应该调用后端接口获取待审批文档列表
    // 由于后端没有专门的待审批列表接口，这里简化处理
    setLoading(true)
    try {
      const res = await documentApi.search({
        status: 'PENDING_APPROVAL' as any,
        page: 0,
        size: 100,
      })
      setDocuments(res.data.data.content || [])
    } catch (error) {
      console.error('加载待审批文档失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleApprove = (document: Document) => {
    setCurrentDocument(document)
    loadApprovalProgress(document.id)
    setApprovalModalVisible(true)
  }

  const loadApprovalProgress = async (documentId: number) => {
    try {
      const res = await approvalApi.getProgress(documentId)
      setApprovalRecords(res.data.data)
    } catch (error) {
      console.error('加载审批进度失败:', error)
    }
  }

  const handleApprovalSubmit = async (values: { status: ApprovalStatus; comment: string }) => {
    if (!currentDocument) return

    try {
      await approvalApi.approve(currentDocument.id, values)
      message.success('审批成功')
      setApprovalModalVisible(false)
      form.resetFields()
      loadTodoDocuments()
    } catch (error) {
      console.error('审批失败:', error)
    }
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
      title: '版本号',
      dataIndex: 'version',
      key: 'version',
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
            查看详情
          </Button>
          <Button type="primary" onClick={() => handleApprove(record)}>
            审批
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Table
        columns={columns}
        dataSource={documents}
        rowKey="id"
        loading={loading}
        pagination={false}
      />

      <Modal
        title="审批文档"
        open={approvalModalVisible}
        onCancel={() => {
          setApprovalModalVisible(false)
          form.resetFields()
        }}
        footer={null}
        width={800}
      >
        {currentDocument && (
          <div>
            <div style={{ marginBottom: 16 }}>
              <strong>文件编号：</strong>
              {currentDocument.fileNumber}
              <br />
              <strong>文件名称：</strong>
              {currentDocument.fileName}
            </div>

            <div style={{ marginBottom: 16 }}>
              <strong>审批进度：</strong>
              {approvalRecords.map((record) => (
                <div key={record.id} style={{ marginTop: 8 }}>
                  <Tag color={record.status === 'APPROVED' ? 'success' : 'processing'}>
                    {record.approverRoleName} - {record.approverName}
                  </Tag>
                  {record.status !== 'PENDING' && (
                    <span style={{ marginLeft: 8 }}>
                      {record.status === 'APPROVED' ? '已同意' : '已驳回'}
                      {record.comment && `：${record.comment}`}
                    </span>
                  )}
                </div>
              ))}
            </div>

            <Form form={form} onFinish={handleApprovalSubmit} layout="vertical">
              <Form.Item
                name="status"
                label="审批结果"
                rules={[{ required: true, message: '请选择审批结果' }]}
              >
                <Select>
                  <Select.Option value={AppStatus.APPROVED}>同意</Select.Option>
                  <Select.Option value={AppStatus.REJECTED}>驳回</Select.Option>
                  <Select.Option value={AppStatus.MODIFY_REQUIRED}>修改后再审</Select.Option>
                </Select>
              </Form.Item>
              <Form.Item name="comment" label="审批意见">
                <TextArea rows={4} placeholder="请输入审批意见" />
              </Form.Item>
              <Form.Item>
                <Space>
                  <Button type="primary" htmlType="submit">
                    提交
                  </Button>
                  <Button onClick={() => setApprovalModalVisible(false)}>取消</Button>
                </Space>
              </Form.Item>
            </Form>
          </div>
        )}
      </Modal>
    </div>
  )
}

