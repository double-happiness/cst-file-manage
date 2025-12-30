package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 角色仓储接口
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色代码查询
     */
    Optional<Role> findByRoleCode(String roleCode);

    /**
     * 查询启用的角色
     */
    List<Role> findByEnabledTrue();

    /**
     * 批量根据ID查询角色（优化N+1查询）
     */
    @Query("SELECT r FROM Role r WHERE r.id IN :ids")
    List<Role> findByIds(@Param("ids") List<Long> ids);
}

