package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据部门ID查询
     */
    List<User> findByDepartmentId(Long departmentId);

    /**
     * 查询启用的用户
     */
    List<User> findByEnabledTrue();

    /**
     * 批量根据ID查询用户（优化N+1查询）
     */
    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findByIds(@Param("ids") List<Long> ids);
}

