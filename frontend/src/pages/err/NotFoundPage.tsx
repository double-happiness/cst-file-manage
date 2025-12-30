import React from 'react'
import { Link } from 'react-router-dom'

const NotFoundPage: React.FC = () => {
    return (
        <div>
            <h1>404 - 页面不存在 😢</h1>
            <Link to="/">返回首页</Link>
        </div>
    )
}

export default NotFoundPage
