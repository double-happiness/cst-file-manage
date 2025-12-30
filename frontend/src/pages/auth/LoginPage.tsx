import {Form, Input, Button, Card, message} from 'antd'
import {UserOutlined, LockOutlined} from '@ant-design/icons'
import {useNavigate} from 'react-router-dom'
import {authApi} from '../../api/auth'
import {useAuthStore} from '../../store/authStore'

export default function LoginPage() {
    const navigate = useNavigate()
    const setAuth = useAuthStore((state) => state.setAuth)

    const onFinish = async (values: { username: string; password: string }) => {
        try {
            const res = await authApi.login(values)

            console.log('res------', res)
            console.log('res.data.data------', res.data)

            if (res.data) {
                // 这里拿到真正的登录信息
                setAuth(res.data)
                message.success('登录成功')
                navigate('/')
            } else {
                message.error(res.message || '登录失败，未获取到登录信息')
            }
        } catch (error) {
            console.error('登录失败:', error)
            message.error('登录请求失败，请检查网络或账户信息')
        }
    }
    return (
        <div
            style={{
                height: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            }}
        >
            <Card
                title={
                    <div style={{textAlign: 'center', fontSize: 24, fontWeight: 'bold'}}>
                        文档管理系统
                    </div>
                }
                style={{width: 400}}
            >
                <Form name="login" onFinish={onFinish} autoComplete="off" size="large">
                    <Form.Item
                        name="username"
                        rules={[{required: true, message: '请输入用户名!'}]}
                    >
                        <Input prefix={<UserOutlined/>} placeholder="用户名"/>
                    </Form.Item>
                    <Form.Item
                        name="password"
                        rules={[{required: true, message: '请输入密码!'}]}
                    >
                        <Input.Password prefix={<LockOutlined/>} placeholder="密码"/>
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit" block>
                            登录
                        </Button>
                    </Form.Item>
                </Form>
            </Card>
        </div>
    )
}

