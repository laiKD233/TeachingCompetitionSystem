package com.teaching.competition.service;

import com.teaching.competition.entity.CompetitionAdmin;

import java.util.List;

/**
 * 竞赛管理员关联服务接口
 */
public interface CompetitionAdminService {

    /**
     * 为管理员分配竞赛
     */
    void assignCompetition(Long adminId, Long competitionId);

    /**
     * 批量为管理员分配竞赛
     */
    void assignCompetitions(Long adminId, List<Long> competitionIds);

    /**
     * 移除管理员的竞赛权限
     */
    void removeCompetition(Long adminId, Long competitionId);

    /**
     * 清空管理员的所有竞赛权限
     */
    void clearAllCompetitions(Long adminId);

    /**
     * 查询管理员管理的竞赛ID列表
     */
    List<Long> getManagedCompetitionIds(Long adminId);

    /**
     * 检查管理员是否有指定竞赛的管理权限
     */
    boolean hasCompetitionPermission(Long adminId, Long competitionId);

    /**
     * 判断管理员是否有所有竞赛的权限（未分配任何竞赛）
     */
    boolean hasAllCompetitionPermission(Long adminId);

    /**
     * 查询指定竞赛的管理员列表
     */
    List<CompetitionAdmin> getAdminsByCompetition(Long competitionId);

    /**
     * 获取管理员管理的竞赛数量
     */
    int getManagedCompetitionCount(Long adminId);
}