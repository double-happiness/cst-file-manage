package com.zsx.cstfilemanage.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 短信服务（示例实现，实际需要对接短信服务商）
 */
@Service
@Slf4j
public class SmsService {

    @Value("${sms.provider:mock}")
    private String provider;

    @Value("${sms.api-key:}")
    private String apiKey;

    /**
     * 发送短信
     */
    public void sendSms(String phone, String content) {
        // 这里实现具体的短信发送逻辑
        // 可以对接阿里云、腾讯云等短信服务
        log.info("发送短信到 {}: {}", phone, content);
        
        // 示例：模拟发送
        if ("mock".equals(provider)) {
            log.info("模拟发送短信（实际环境需要对接短信服务商）");
        } else {
            // 实际调用短信服务API
            // TODO: 实现具体的短信发送逻辑
        }
    }
}

