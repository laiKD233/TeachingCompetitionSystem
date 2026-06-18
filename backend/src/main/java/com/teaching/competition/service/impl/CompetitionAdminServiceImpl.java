package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.competition.entity.CompetitionAdmin;
import com.teaching.competition.mapper.CompetitionAdminMapper;
import com.teaching.competition.service.CompetitionAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 竞赛管理员关联服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompetitionAdminServiceImpl implements CompetitionAdminService {

    private final CompetitionAdminMapper competitionAdminMapper;

    @Override
    @Transactional
    public void assignCompetition(Long adminId, Long competitionId) {
        // 检查是否已存在
        Integer exists = competitionAdminMapper.checkAdminCompetition(adminId, competitionId);
        if (exists != null && exists > 0) {
            return;
        }

        CompetitionAdmin competitionAdmin = new CompetitionAdmin();
        competitionAdmin.setAdminId(adminId);
        competitionAdmin.setCompetitionId(competitionId);
        competitionAdminMapper.insert(competitionAdmin);
    }

    @Override
    @Transactional
    public void assignCompetitions(Long adminId, List<Long> competitionIds) {
        if (competitionIds == null || competitionIds.isEmpty()) {
            return;
        }

        for (Long competitionId : competitionIds) {
            assignCompetition(adminId, competitionId);
        }
    }

    @Override
    @Transactional
    public void removeCompetition(Long adminId, Long competitionId) {
        LambdaQueryWrapper<CompetitionAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompetitionAdmin::getAdminId, adminId)
               .eq(CompetitionAdmin::getCompetitionId, competitionId);
        competitionAdminMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void clearAllCompetitions(Long adminId) {
        LambdaQueryWrapper<CompetitionAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompetitionAdmin::getAdminId, adminId);
        competitionAdminMapper.delete(wrapper);
    }

    @Override
    public List<Long> getManagedCompetitionIds(Long adminId) {
        return competitionAdminMapper.selectCompetitionIdsByAdminId(adminId);
    }

    @Override
    public boolean hasCompetitionPermission(Long adminId, Long competitionId) {
        // 如果管理员没有分配任何竞赛，则拥有所有竞赛权限
        if (hasAllCompetitionPermission(adminId)) {
            return true;
        }

        Integer count = competitionAdminMapper.checkAdminCompetition(adminId, competitionId);
        return count != null && count > 0;
    }

    @Override
    public boolean hasAllCompetitionPermission(Long adminId) {
        Integer count = competitionAdminMapper.countByAdminId(adminId);
        return count == null || count == 0;
    }

    @Override
    public List<CompetitionAdmin> getAdminsByCompetition(Long competitionId) {
        return competitionAdminMapper.selectByCompetitionId(competitionId);
    }

    @Override
    public int getManagedCompetitionCount(Long adminId) {
        Integer count = competitionAdminMapper.countByAdminId(adminId);
        return count != null ? count : 0;
    }
}