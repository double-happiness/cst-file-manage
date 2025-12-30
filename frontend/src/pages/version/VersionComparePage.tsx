import { useState } from 'react'
import { Card, Select, Button, Space, Typography, Divider, message } from 'antd'
import { BranchesOutlined } from '@ant-design/icons'
import { versionApi } from '../../api/version'
import { documentApi } from '../../api/document'
import type { Document } from '../../api/types'

const { Title, Paragraph } = Typography

export default function VersionComparePage() {
  const [version1Id, setVersion1Id] = useState<number | undefined>()
  const [version2Id, setVersion2Id] = useState<number | undefined>()
  const [versions, setVersions] = useState<Document[]>([])
  const [compareResult, setCompareResult] = useState<any>(null)
  const [loading, setLoading] = useState(false)

  const loadVersions = async (fileNumber: string) => {
    try {
      const res = await versionApi.getVersions(fileNumber)
      // 后端返回格式: ApiResponse<List<Document>>，res.data 就是 List<Document>
      const versionList = Array.isArray(res.data) ? res.data : []
      setVersions(versionList)
    } catch (error) {
      console.error('加载版本列表失败:', error)
      setVersions([])
    }
  }

  const handleCompare = async () => {
    if (!version1Id || !version2Id) {
      message.warning('请选择两个版本进行对比')
      return
    }

    setLoading(true)
    try {
      const res = await versionApi.compare(version1Id, version2Id)
      // 后端返回格式: ApiResponse<VersionComparisonResult>，res.data 就是 VersionComparisonResult
      setCompareResult(res.data)
    } catch (error) {
      console.error('版本对比失败:', error)
      message.error('版本对比失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <Card title="版本对比">
        <Space direction="vertical" style={{ width: '100%' }} size="large">
          <div>
            <span style={{ marginRight: 8 }}>文件编号：</span>
            <Select
              style={{ width: 200 }}
              placeholder="选择文件编号"
              showSearch
              onSearch={(value) => {
                if (value) {
                  loadVersions(value)
                }
              }}
            />
          </div>

          <div>
            <Space>
              <Select
                style={{ width: 200 }}
                placeholder="选择版本1"
                value={version1Id}
                onChange={setVersion1Id}
              >
                {(versions || []).map((v) => (
                  <Select.Option key={v.id} value={v.id}>
                    {v.version}
                  </Select.Option>
                ))}
              </Select>
              <span>VS</span>
              <Select
                style={{ width: 200 }}
                placeholder="选择版本2"
                value={version2Id}
                onChange={setVersion2Id}
              >
                {(versions || []).map((v) => (
                  <Select.Option key={v.id} value={v.id}>
                    {v.version}
                  </Select.Option>
                ))}
              </Select>
              <Button type="primary" icon={<BranchesOutlined />} onClick={handleCompare} loading={loading}>
                对比
              </Button>
            </Space>
          </div>

          {compareResult && (
            <Card>
              <Title level={4}>对比结果</Title>
              <Divider />
              <Paragraph>
                <pre style={{ whiteSpace: 'pre-wrap', background: '#f5f5f5', padding: 16 }}>
                  {compareResult.differences}
                </pre>
              </Paragraph>
            </Card>
          )}
        </Space>
      </Card>
    </div>
  )
}

