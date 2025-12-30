package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.model.entity.User;
import com.zsx.cstfilemanage.domain.repository.UserRepository;
import com.zsx.cstfilemanage.infrastructure.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * 登录
     */
    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BizException(ErrorCode.LOGIN_FAILED));

        if (!user.getEnabled()) {
            throw new BizException(ErrorCode.USER_DISABLED);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException(ErrorCode.LOGIN_FAILED);
        }

        // 生成Token
        String token = tokenProvider.generateToken(user.getId(), user.getUsername(), user.getRealName());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setExpiresIn(86400L); // 24小时

        return response;
    }

    /**
     * 登录响应
     */
    public static class LoginResponse {
        private String token;
        private Long userId;
        private String username;
        private String realName;
        private Long expiresIn;

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public Long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    }
}

