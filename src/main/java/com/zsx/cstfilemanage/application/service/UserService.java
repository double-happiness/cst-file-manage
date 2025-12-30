package com.zsx.cstfilemanage.application.service;

import com.zsx.cstfilemanage.common.exception.BizException;
import com.zsx.cstfilemanage.common.exception.ErrorCode;
import com.zsx.cstfilemanage.domain.model.entity.User;
import com.zsx.cstfilemanage.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务
 */
@Slf4j
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
        log.debug("=== UserService.createUser 开始 ===");
        log.info("创建用户 - 用户名: {}, 真实姓名: {}, 部门: {}, 邮箱: {}", 
                user.getUsername(), user.getRealName(), user.getDepartmentName(), user.getEmail());
        
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.warn("创建用户失败 - 用户名已存在: {}", user.getUsername());
            throw new BizException(ErrorCode.USERNAME_EXISTS);
        }
        
        log.debug("创建用户 - 原始密码长度: {}", user.getPassword() != null ? user.getPassword().length() : 0);
        // 加密密码
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        log.debug("创建用户 - 密码已加密");
        
        User saved = userRepository.save(user);
        log.info("创建用户成功 - 用户ID: {}, 用户名: {}", saved.getId(), saved.getUsername());
        log.debug("=== UserService.createUser 结束 ===");
        return saved;
    }

    /**
     * 更新用户
     */
    @Transactional
    public User updateUser(Long userId, User user) {
        log.debug("=== UserService.updateUser 开始 ===");
        log.info("更新用户 - 用户ID: {}, 用户名: {}", userId, user.getUsername());
        
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("更新用户失败 - 用户不存在: {}", userId);
                    return new BizException(ErrorCode.USER_NOT_FOUND);
                });

        log.debug("更新用户 - 原始用户信息 - 真实姓名: {}, 部门: {}, 邮箱: {}", 
                existingUser.getRealName(), existingUser.getDepartmentName(), existingUser.getEmail());

        // 更新字段
        if (user.getRealName() != null) {
            existingUser.setRealName(user.getRealName());
            log.debug("更新用户 - 真实姓名: {}", user.getRealName());
        }
        if (user.getDepartmentId() != null) {
            existingUser.setDepartmentId(user.getDepartmentId());
            log.debug("更新用户 - 部门ID: {}", user.getDepartmentId());
        }
        if (user.getDepartmentName() != null) {
            existingUser.setDepartmentName(user.getDepartmentName());
            log.debug("更新用户 - 部门名称: {}", user.getDepartmentName());
        }
        if (user.getPosition() != null) {
            existingUser.setPosition(user.getPosition());
            log.debug("更新用户 - 岗位: {}", user.getPosition());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
            log.debug("更新用户 - 电话: {}", user.getPhone());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
            log.debug("更新用户 - 邮箱: {}", user.getEmail());
        }
        if (user.getEnabled() != null) {
            existingUser.setEnabled(user.getEnabled());
            log.debug("更新用户 - 启用状态: {}", user.getEnabled());
        }

        User saved = userRepository.save(existingUser);
        log.info("更新用户成功 - 用户ID: {}, 用户名: {}", saved.getId(), saved.getUsername());
        log.debug("=== UserService.updateUser 结束 ===");
        return saved;
    }

    /**
     * 重置密码
     */
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        log.debug("=== UserService.resetPassword 开始 ===");
        log.info("重置密码 - 用户ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("重置密码失败 - 用户不存在: {}", userId);
                    return new BizException(ErrorCode.USER_NOT_FOUND);
                });

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        
        log.info("重置密码成功 - 用户ID: {}, 用户名: {}", userId, user.getUsername());
        log.debug("=== UserService.resetPassword 结束 ===");
    }

    /**
     * 查询所有用户
     */
    public List<User> getAllUsers() {
        log.debug("=== UserService.getAllUsers 开始 ===");
        List<User> users = userRepository.findAll();
        log.info("查询所有用户成功 - 用户数量: {}", users.size());
        log.debug("=== UserService.getAllUsers 结束 ===");
        return users;
    }

    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        log.debug("=== UserService.getUserById 开始 ===");
        log.info("查询用户 - 用户ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("查询用户失败 - 用户不存在: {}", id);
                    return new BizException(ErrorCode.USER_NOT_FOUND);
                });
        
        log.info("查询用户成功 - 用户ID: {}, 用户名: {}, 真实姓名: {}", 
                user.getId(), user.getUsername(), user.getRealName());
        log.debug("=== UserService.getUserById 结束 ===");
        return user;
    }
}

