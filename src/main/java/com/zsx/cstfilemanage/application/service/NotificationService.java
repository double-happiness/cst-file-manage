package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.domain.model.entity.Document;
import com.zsx.cstfilemanage.domain.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知服务
 */
@Service
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    private final SmsService smsService;

    @Value("${spring.mail.from:noreply@example.com}")
    private String fromEmail;

    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;

    public NotificationService(JavaMailSender mailSender, SmsService smsService) {
        this.mailSender = mailSender;
        this.smsService = smsService;
    }

    /**
     * 发送审批通知
     */
    public void sendApprovalNotification(User approver, Document document) {
        String subject = "待审批文档通知";
        String content = String.format(
            "您好 %s，\n\n" +
            "您有一个待审批的文档：\n" +
            "文件编号：%s\n" +
            "文件名称：%s\n" +
            "上传人：%s\n" +
            "请及时登录系统进行审批。\n\n" +
            "系统自动发送，请勿回复。",
            approver.getRealName(),
            document.getFileNumber(),
            document.getFileName(),
            document.getCompilerName()
        );

        sendEmail(approver.getEmail(), subject, content);
        if (smsEnabled && approver.getPhone() != null) {
            sendSms(approver.getPhone(), content);
        }
    }

    /**
     * 发送审批通过通知
     */
    public void sendApprovalPassedNotification(User uploader, Document document) {
        String subject = "文档审批通过通知";
        String content = String.format(
            "您好 %s，\n\n" +
            "您的文档已通过审批：\n" +
            "文件编号：%s\n" +
            "文件名称：%s\n" +
            "文档状态：已批准\n" +
            "现在可以进行文件下发操作。\n\n" +
            "系统自动发送，请勿回复。",
            uploader.getRealName(),
            document.getFileNumber(),
            document.getFileName()
        );

        sendEmail(uploader.getEmail(), subject, content);
        if (smsEnabled && uploader.getPhone() != null) {
            sendSms(uploader.getPhone(), content);
        }
    }

    /**
     * 发送审批驳回通知
     */
    public void sendApprovalRejectedNotification(User uploader, Document document, String reason) {
        String subject = "文档审批驳回通知";
        String content = String.format(
            "您好 %s，\n\n" +
            "您的文档审批被驳回：\n" +
            "文件编号：%s\n" +
            "文件名称：%s\n" +
            "驳回原因：%s\n" +
            "请修改后重新提交审批。\n\n" +
            "系统自动发送，请勿回复。",
            uploader.getRealName(),
            document.getFileNumber(),
            document.getFileName(),
            reason != null ? reason : "无"
        );

        sendEmail(uploader.getEmail(), subject, content);
        if (smsEnabled && uploader.getPhone() != null) {
            sendSms(uploader.getPhone(), content);
        }
    }

    /**
     * 发送文件下发通知
     */
    public void sendDistributionNotification(List<User> receivers, Document document, String distributorName) {
        String subject = "文件下发通知";
        String content = String.format(
            "您好，\n\n" +
            "您收到一个下发的文件：\n" +
            "文件编号：%s\n" +
            "文件名称：%s\n" +
            "下发人：%s\n" +
            "请及时登录系统查看。\n\n" +
            "系统自动发送，请勿回复。",
            document.getFileNumber(),
            document.getFileName(),
            distributorName
        );

        for (User receiver : receivers) {
            sendEmail(receiver.getEmail(), subject, content);
            if (smsEnabled && receiver.getPhone() != null) {
                sendSms(receiver.getPhone(), content);
            }
        }
    }

    /**
     * 发送邮件
     */
    private void sendEmail(String to, String subject, String content) {
        if (!emailEnabled || to == null || to.isEmpty()) {
            log.debug("邮件发送已禁用或收件人为空，跳过发送");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("邮件发送成功: {}", to);
        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送短信
     */
    private void sendSms(String phone, String content) {
        if (!smsEnabled || phone == null || phone.isEmpty()) {
            log.debug("短信发送已禁用或手机号为空，跳过发送");
            return;
        }

        try {
            smsService.sendSms(phone, content);
            log.info("短信发送成功: {}", phone);
        } catch (Exception e) {
            log.error("短信发送失败: {}", e.getMessage(), e);
        }
    }
}

