package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 角色权限关联仓储接口
 */
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    /**
     * 根据角色ID查询权限ID列表
     */
    @Query("SELECT rp.permissionId FROM RolePermission rp WHERE rp.roleId = :roleId")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID查询角色ID列表
     */
    @Query("SELECT rp.roleId FROM RolePermission rp WHERE rp.permissionId = :permissionId")
    List<Long> findRoleIdsByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 根据角色ID查询
     */
    List<RolePermission> findByRoleId(Long roleId);

    /**
     * 根据权限ID查询
     */
    List<RolePermission> findByPermissionId(Long permissionId);

    /**
     * 删除角色的所有权限
     */
    void deleteByRoleId(Long roleId);

    /**
     * 检查角色是否拥有指定权限
     */
    @Query("SELECT COUNT(rp) > 0 FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId")
    boolean existsByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}

