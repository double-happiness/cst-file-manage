package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 用户组仓储接口
 */
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    /**
     * 根据用户组代码查询
     */
    Optional<UserGroup> findByGroupCode(String groupCode);

    /**
     * 查询启用的用户组
     */
    List<UserGroup> findByEnabledTrue();
}

