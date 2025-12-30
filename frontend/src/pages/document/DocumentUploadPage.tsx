import { useState } from 'react'
import { Form, Input, DatePicker, Space, Upload, Button, Card, message, Select } from 'antd'
import { UploadOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import dayjs from 'dayjs'
import { documentApi } from '../../api/document'
import type { UploadFile } from 'antd'


const { TextArea } = Input

export default function DocumentUploadPage() {
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [fileList, setFileList] = useState<UploadFile[]>([])
  const [loading, setLoading] = useState(false)

  const onFinish = async (values: any) => {
    if (fileList.length === 0) {
      message.warning('请选择要上传的文件')
      return
    }

    setLoading(true)
    try {
      const file = fileList[0].originFileObj
      if (!file) {
        message.error('文件不存在')
        return
      }

      await documentApi.upload({
        file,
        fileNumber: values.fileNumber,
        fileName: values.fileName,
        productModel: values.productModel,
        version: values.version,
        compileDate: values.compileDate.format('YYYY-MM-DD HH:mm:ss'),
        description: values.description,
      })

      message.success('上传成功')
      form.resetFields()
      setFileList([])
      navigate('/documents')
    } catch (error) {
      console.error('上传失败:', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card title="上传文档">
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        initialValues={{
          compileDate: dayjs(),
        }}
      >
        <Form.Item
          name="file"
          label="文件"
          rules={[{ required: true, message: '请选择文件' }]}
        >
          <Upload
            fileList={fileList}
            beforeUpload={() => false}
            onChange={({ fileList }) => setFileList(fileList)}
            maxCount={1}
          >
            <Button icon={<UploadOutlined />}>选择文件</Button>
          </Upload>
        </Form.Item>
        <Form.Item
          name="fileNumber"
          label="文件编号"
          rules={[{ required: true, message: '请输入文件编号' }]}
        >
          <Input placeholder="请输入文件编号" />
        </Form.Item>
        <Form.Item
          name="fileName"
          label="文件名称"
          rules={[{ required: true, message: '请输入文件名称' }]}
        >
          <Input placeholder="请输入文件名称" />
        </Form.Item>
        <Form.Item name="productModel" label="所属产品型号">
          <Input placeholder="请输入产品型号" />
        </Form.Item>
        <Form.Item
          name="version"
          label="版本号"
          rules={[{ required: true, message: '请输入版本号' }]}
        >
          <Input placeholder="例如：V1.0" />
        </Form.Item>
        <Form.Item
          name="compileDate"
          label="编制日期"
          rules={[{ required: true, message: '请选择编制日期' }]}
        >
          <DatePicker showTime style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item name="description" label="文件描述">
          <TextArea rows={4} placeholder="请输入文件描述" />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              提交上传
            </Button>
            <Button onClick={() => navigate('/documents')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </Card>
  )
}

