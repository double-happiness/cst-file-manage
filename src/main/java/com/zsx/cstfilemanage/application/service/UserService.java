package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.model.entity.User;
import com.zsx.cstfilemanage.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 创建用户
     */
    @Transactional
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BizException(ErrorCode.USERNAME_EXISTS);
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * 更新用户
     */
    @Transactional
    public User updateUser(Long userId, User user) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));

        // 更新字段
        if (user.getRealName() != null) {
            existingUser.setRealName(user.getRealName());
        }
        if (user.getDepartmentId() != null) {
            existingUser.setDepartmentId(user.getDepartmentId());
        }
        if (user.getDepartmentName() != null) {
            existingUser.setDepartmentName(user.getDepartmentName());
        }
        if (user.getPosition() != null) {
            existingUser.setPosition(user.getPosition());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getEnabled() != null) {
            existingUser.setEnabled(user.getEnabled());
        }

        return userRepository.save(existingUser);
    }

    /**
     * 重置密码
     */
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 查询所有用户
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));
    }
}

