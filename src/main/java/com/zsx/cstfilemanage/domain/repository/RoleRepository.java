package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

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
}

