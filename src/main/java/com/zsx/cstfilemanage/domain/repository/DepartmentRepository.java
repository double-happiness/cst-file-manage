package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 部门仓储接口
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * 根据部门代码查询
     */
    Optional<Department> findByDepartmentCode(String departmentCode);

    /**
     * 根据父部门ID查询
     */
    List<Department> findByParentId(Long parentId);

    /**
     * 查询启用的部门
     */
    List<Department> findByEnabledTrue();
}

