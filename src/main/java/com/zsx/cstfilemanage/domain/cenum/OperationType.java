package com.zsx.cstfilemanage.domain.cenum;

/**
 * 操作类型枚举
 */
public enum OperationType {
    LOGIN("登录"),
    LOGOUT("登出"),
    UPLOAD("上传文件"),
    DOWNLOAD("下载文件"),
    APPROVE("审批文件"),
    REJECT("驳回文件"),
    DISTRIBUTE("下发文件"),
    RECALL("回收文件"),
    OBSOLETE("作废文件"),
    CREATE_VERSION("创建版本"),
    VIEW("查看文件"),
    DELETE("删除文件"),
    MODIFY("修改文件");

    private final String description;

    OperationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

