package com.zsx.cstfilemanage.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    UNAUTHORIZED(401, "未登录或登录已过期"),
    FILE_TYPE_NOT_ALLOWED(1001, "文件类型不允许"),
    FILE_TOO_LARGE(1002, "文件过大"),
    BIZ_TYPE_NOT_SUPPORTED(1003, "业务类型不支持"),
    UPLOAD_TOO_FREQUENT(1004, "上传太频繁"),
    FILE_SIZE_INVALID(1004, "上传太频繁"),
    FILE_NUMBER_EXISTS(1005, "文件编号已存在"),
    DOCUMENT_NOT_FOUND(1006, "文档不存在"),
    DOCUMENT_STATUS_INVALID(1007, "文档状态不正确，无法提交审批"),
    APPROVAL_FLOW_NOT_FOUND(1008, "未找到适用的审批流程"),
    APPROVER_NOT_FOUND(1009, "未找到审批人"),
    USER_NOT_FOUND(1010, "用户不存在"),
    APPROVAL_RECORD_NOT_FOUND(1011, "未找到待审批记录"),
    APPROVAL_FLOW_CONFIG_ERROR(1012, "审批流程配置错误"),
    DOCUMENT_NOT_APPROVED(1013, "只能下发已批准的文档"),
    NOT_CURRENT_VERSION(1014, "只能下发当前有效版本的文档"),
    DISTRIBUTION_TARGET_ERROR(1015, "下发对象数据格式错误"),
    VERSION_EXISTS(1016, "版本号已存在"),
    VERSION_ALREADY_CURRENT(1017, "该版本已经是当前版本"),
    USERNAME_EXISTS(1018, "用户名已存在"),
    LOGIN_FAILED(1019, "用户名或密码错误"),
    USER_DISABLED(1020, "用户已被禁用"),
    FILE_NOT_FOUND(1021, "文件不存在"),
    NOT_PDF_FILE(1022, "文件类型不是PDF"),
    NOT_IMAGE_FILE(1023, "文件类型不是图片"),
    ROLE_NOT_FOUND(1024, "角色不存在"),
    PERMISSION_NOT_FOUND(1025, "权限不存在"),
    PERMISSION_CODE_EXISTS(1026, "权限代码已存在"),
    ROLE_CODE_EXISTS(1027, "角色代码已存在"),
    ROLE_IN_USE(1028, "该角色正在被使用，无法删除"),
    USERGROUP_CODE_EXISTS(1029, "用户组代码已存在"),
    USERGROUP_NOT_FOUND(1030, "用户组不存在"),
    PERMISSION_DENIED(1031, "没有权限"),
    INTERNAL_ERROR(500, "系统内部错误");

    private final int code;
    private final String message;
}
