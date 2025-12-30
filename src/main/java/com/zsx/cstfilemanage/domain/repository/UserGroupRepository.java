package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * 批量根据ID查询用户组（优化N+1查询）
     */
    @Query("SELECT ug FROM UserGroup ug WHERE ug.id IN :ids")
    List<UserGroup> findByIds(@Param("ids") List<Long> ids);
}

