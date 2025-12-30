import { useState, useEffect } from 'react'
import { Table, Tag } from 'antd'
import { approvalApi } from '../../api/approval'
import { documentApi } from '../../api/document'
import type { ApprovalRecord } from '../../api/types'
import { ApprovalStatus } from '../../api/types'

export default function ApprovalHistoryPage() {
  const [loading, setLoading] = useState(false)
  const [records, setRecords] = useState<ApprovalRecord[]>([])

  useEffect(() => {
    // 这里应该调用后端接口获取审批历史
    // 简化处理，实际应该有一个专门的审批历史接口
    loadHistory()
  }, [])

  const loadHistory = async () => {
    setLoading(true)
    try {
      // 获取所有已审批的文档，然后获取它们的审批记录
      const res = await documentApi.search({
        page: 0,
        size: 100,
      })
      // 这里简化处理，实际应该调用专门的审批历史接口
      setRecords([])
    } catch (error) {
      console.error('加载审批历史失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const columns = [
    {
      title: '文档编号',
      dataIndex: 'documentId',
      key: 'documentId',
    },
    {
      title: '审批环节',
      dataIndex: 'stepOrder',
      key: 'stepOrder',
    },
    {
      title: '审批人',
      dataIndex: 'approverName',
      key: 'approverName',
    },
    {
      title: '审批角色',
      dataIndex: 'approverRoleName',
      key: 'approverRoleName',
    },
    {
      title: '审批状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: ApprovalStatus) => {
        const statusMap: Record<ApprovalStatus, { color: string; text: string }> = {
          PENDING: { color: 'processing', text: '待审批' },
          APPROVED: { color: 'success', text: '同意' },
          REJECTED: { color: 'error', text: '驳回' },
          MODIFY_REQUIRED: { color: 'warning', text: '修改后再审' },
        }
        const info = statusMap[status]
        return <Tag color={info.color}>{info.text}</Tag>
      },
    },
    {
      title: '审批意见',
      dataIndex: 'comment',
      key: 'comment',
    },
    {
      title: '审批时间',
      dataIndex: 'approveTime',
      key: 'approveTime',
      render: (time: string) => (time ? new Date(time).toLocaleString('zh-CN') : '-'),
    },
  ]

  return (
    <div>
      <Table
        columns={columns}
        dataSource={records}
        rowKey="id"
        loading={loading}
        pagination={false}
      />
    </div>
  )
}

