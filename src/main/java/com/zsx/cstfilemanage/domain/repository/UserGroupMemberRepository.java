package com.zsx.cstfilemanage.domain.repository;

import com.zsx.cstfilemanage.domain.model.entity.UserGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 用户组成员仓储接口
 */
public interface UserGroupMemberRepository extends JpaRepository<UserGroupMember, Long> {

    /**
     * 根据用户组ID查询成员
     */
    List<UserGroupMember> findByGroupId(Long groupId);

    /**
     * 根据用户ID查询用户组
     */
    List<UserGroupMember> findByUserId(Long userId);

    /**
     * 根据用户组ID和用户ID查询
     */
    Optional<UserGroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    /**
     * 根据用户组ID查询用户ID列表
     */
    @Query("SELECT ugm.userId FROM UserGroupMember ugm WHERE ugm.groupId = :groupId")
    List<Long> findUserIdsByGroupId(@Param("groupId") Long groupId);

    /**
     * 根据用户ID查询用户组ID列表
     */
    @Query("SELECT ugm.groupId FROM UserGroupMember ugm WHERE ugm.userId = :userId")
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);
}

