package com.zsx.cstfilemanage.config;


import com.zsx.cstfilemanage.domain.storage.OcsClient;
import com.zsx.cstfilemanage.domain.storage.UploadToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcsConfig {

    @Bean
    public OcsClient ocsClient() {
        return new OcsClient() {
            @Override
            public UploadToken generateUploadToken(String objectKey, int expireSeconds) {
                return null;
            }
        };
        // return new OcsClient("endpoint", "accessKeyId", "accessKeySecret");
    }
}
