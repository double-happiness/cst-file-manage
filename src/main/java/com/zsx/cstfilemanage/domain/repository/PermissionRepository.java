package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 权限仓储接口
 */
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 根据权限代码查询
     */
    Permission findByPermissionCode(String permissionCode);

    /**
     * 根据父权限ID查询
     */
    List<Permission> findByParentId(Long parentId);

    /**
     * 查询根权限（parentId为null）
     */
    List<Permission> findByParentIdIsNull();
}

