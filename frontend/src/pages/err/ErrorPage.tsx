import React from 'react'
import { useRouteError } from 'react-router-dom'

const ErrorPage: React.FC = () => {
    const error = useRouteError() as any

    console.error('全局错误:', error)

    return (
        <div>
            <h1>发生错误 😢</h1>
            <p>{error?.message || '未知错误'}</p>
        </div>
    )
}

export default ErrorPage
