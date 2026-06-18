package com.teaching.competition.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.CompetitionDTO;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.CompetitionMapper;
import com.teaching.competition.service.LocalFileService;
import com.teaching.competition.service.CompetitionAdminService;
import com.teaching.competition.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {

    private final CompetitionAdminService competitionAdminService;
    private final CompetitionStatusResolver statusResolver;
    private final JdbcTemplate jdbcTemplate;
    private final LocalFileService localFileService;

    @Override
    public PageResult<Competition> getPublicList(String keyword, String status, String type, int page, int size) {
        Page<Competition> competitionPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(Competition::getStatus, "DRAFT");
        
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq(Competition::getType, type);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Competition::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Competition::getName, keyword)
                    .or().like(Competition::getTheme, keyword));
        }
        wrapper.orderByDesc(Competition::getCreatedAt);
        
        Page<Competition> result = page(competitionPage, wrapper);
        refreshResultStatuses(result.getRecords());
        
        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }

    @Override
    public Competition getPublicDetail(Long id) {
        Competition competition = getById(id);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        refreshAndPersistStatus(competition);
        if ("DRAFT".equals(competition.getStatus())) {
            throw new BusinessException("竞赛未发布");
        }
        return competition;
    }

    @Override
    public PageResult<Competition> getAdminList(String keyword, String status, int page, int size, Long adminId, String role) {
        Page<Competition> competitionPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<>();
        
        // 添加权限过滤
        addPermissionFilter(wrapper, adminId, role);
        
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Competition::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Competition::getName, keyword)
                    .or().like(Competition::getTheme, keyword));
        }
        wrapper.orderByDesc(Competition::getCreatedAt);
        
        Page<Competition> result = page(competitionPage, wrapper);
        refreshResultStatuses(result.getRecords());
        
        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }

    @Override
    public Competition getAdminDetail(Long id, Long adminId, String role) {
        Competition competition = getById(id);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        refreshAndPersistStatus(competition);
        
        // 检查权限
        if (!hasFullCompetitionAccess(role) && !competitionAdminService.hasCompetitionPermission(adminId, id)) {
            throw new BusinessException("无权访问该竞赛");
        }
        
        return competition;
    }

    @Override
    public PageResult<Competition> getFinishedCompetitions(int page, int size, Long adminId, String role) {
        Page<Competition> competitionPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<>();
        
        // 添加权限过滤
        addPermissionFilter(wrapper, adminId, role);
        
        wrapper.in(Competition::getStatus, "FINISHED", "ENDED")
                .orderByDesc(Competition::getAnnouncementEnd);
        
        Page<Competition> result = page(competitionPage, wrapper);
        refreshResultStatuses(result.getRecords());
        
        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }

    /**
     * 添加权限过滤条件
     */
    private void addPermissionFilter(LambdaQueryWrapper<Competition> wrapper, Long adminId, String role) {
        if (hasFullCompetitionAccess(role)) {
            return;
        }

        // 获取管理员管理的竞赛ID列表
        List<Long> managedCompetitionIds = competitionAdminService.getManagedCompetitionIds(adminId);
        
        // 如果管理员有分配特定竞赛，则只返回这些竞赛
        if (managedCompetitionIds != null && !managedCompetitionIds.isEmpty()) {
            wrapper.in(Competition::getId, managedCompetitionIds);
        }
        // 如果没有分配任何竞赛，则返回所有竞赛（默认拥有全部权限）
    }

    private boolean hasFullCompetitionAccess(String role) {
        return "ADMIN".equals(role);
    }

    @Override
    @Transactional
    public void createCompetition(CompetitionDTO dto, Long userId, String role) {
        Competition competition = new Competition();
        BeanUtils.copyProperties(dto, competition);
        competition.setParticipationType(normalizeParticipationType(dto.getParticipationType()));
        competition.setCreatedBy(userId);
        validateCompetitionTimeline(competition);
        statusResolver.refreshStatus(competition);
        save(competition);
        if (!hasFullCompetitionAccess(role)) {
            competitionAdminService.assignCompetition(userId, competition.getId());
        }
    }

    @Override
    @Transactional
    public void updateCompetition(Long id, CompetitionDTO dto, Long adminId, String role) {
        Competition competition = getById(id);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        
        // 检查权限
        if (!hasFullCompetitionAccess(role) && !competitionAdminService.hasCompetitionPermission(adminId, id)) {
            throw new BusinessException("无权修改该竞赛");
        }
        
        BeanUtils.copyProperties(dto, competition, "id", "createdBy", "createdAt", "status");
        competition.setParticipationType(normalizeParticipationType(dto.getParticipationType()));
        validateCompetitionTimeline(competition);
        statusResolver.refreshStatus(competition);
        updateById(competition);
    }

    @Override
    @Transactional
    public void deleteCompetition(Long id, Long adminId, String role) {
        Competition competition = getById(id);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        
        // 检查权限
        if (!hasFullCompetitionAccess(role) && !competitionAdminService.hasCompetitionPermission(adminId, id)) {
            throw new BusinessException("无权删除该竞赛");
        }
        
        cleanupCompetitionRelations(id);
        removeById(id);
    }

    @Override
    public List<Competition> getAllCompetitions() {
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Competition::getCreatedAt);
        return list(wrapper);
    }

    private String normalizeParticipationType(String participationType) {
        if (StrUtil.isBlank(participationType)) {
            return "INDIVIDUAL";
        }
        Set<String> allowedTypes = Set.of("INDIVIDUAL", "TEAM");
        if (!allowedTypes.contains(participationType)) {
            throw new BusinessException("参赛方式仅支持个人赛或团队赛");
        }
        return participationType;
    }

    private void refreshResultStatuses(List<Competition> competitions) {
        if (competitions == null || competitions.isEmpty()) {
            return;
        }
        for (Competition competition : competitions) {
            refreshAndPersistStatus(competition);
        }
    }

    private void refreshAndPersistStatus(Competition competition) {
        String oldStatus = competition.getStatus();
        statusResolver.refreshStatus(competition);
        if (!competition.getStatus().equals(oldStatus)) {
            updateById(competition);
        }
    }

    private void validateCompetitionTimeline(Competition competition) {
        ensureNotAfter("报名开始时间", competition.getRegistrationStart(), "报名截止时间", competition.getRegistrationEnd());
        ensureNotAfter("报名截止时间", competition.getRegistrationEnd(), "作品提交截止时间", competition.getSubmissionDeadline());
        ensureNotAfter("作品提交截止时间", competition.getSubmissionDeadline(), "评审开始时间", competition.getReviewStart());
        ensureNotAfter("评审开始时间", competition.getReviewStart(), "评审结束时间", competition.getReviewEnd());
        ensureNotAfter("评审结束时间", competition.getReviewEnd(), "公示开始时间", competition.getAnnouncementStart());
        ensureNotAfter("公示开始时间", competition.getAnnouncementStart(), "公示结束时间", competition.getAnnouncementEnd());
    }

    private void ensureNotAfter(String startName, LocalDateTime start, String endName, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new BusinessException(startName + "不能晚于" + endName);
        }
    }

    private void cleanupCompetitionRelations(Long competitionId) {
        List<String> fileUrls = jdbcTemplate.queryForList(
                "SELECT file_url FROM work WHERE competition_id = ? AND deleted = 0 AND file_url IS NOT NULL AND file_url <> ''",
                String.class,
                competitionId
        );
        for (String fileUrl : fileUrls) {
            localFileService.delete(fileUrl);
        }

        List<Long> teamIds = jdbcTemplate.queryForList(
                "SELECT id FROM team WHERE competition_id = ? AND deleted = 0",
                Long.class,
                competitionId
        );
        for (Long teamId : teamIds) {
            cleanupTeamRelations(teamId);
        }

        jdbcTemplate.update(
                "UPDATE award SET deleted = 1 WHERE deleted = 0 AND " +
                        "(competition_id = ? OR work_id IN (SELECT id FROM work WHERE competition_id = ?))",
                competitionId,
                competitionId
        );
        jdbcTemplate.update(
                "UPDATE review_task SET deleted = 1 WHERE deleted = 0 AND " +
                        "(competition_id = ? OR work_id IN (SELECT id FROM work WHERE competition_id = ?))",
                competitionId,
                competitionId
        );
        jdbcTemplate.update("UPDATE work SET deleted = 1 WHERE deleted = 0 AND competition_id = ?", competitionId);
        jdbcTemplate.update("UPDATE registration SET deleted = 1 WHERE deleted = 0 AND competition_id = ?", competitionId);
        jdbcTemplate.update("DELETE FROM track WHERE competition_id = ?", competitionId);
        jdbcTemplate.update("UPDATE competition_admin SET deleted = 1 WHERE deleted = 0 AND competition_id = ?", competitionId);
    }

    private void cleanupTeamRelations(Long teamId) {
        jdbcTemplate.update("UPDATE team_member SET deleted = 1 WHERE deleted = 0 AND team_id = ?", teamId);
        jdbcTemplate.update("UPDATE team_advisor SET deleted = 1 WHERE deleted = 0 AND team_id = ?", teamId);
        jdbcTemplate.update("UPDATE team_advisor_audit SET deleted = 1 WHERE deleted = 0 AND team_id = ?", teamId);
        jdbcTemplate.update("UPDATE team_task SET deleted = 1 WHERE deleted = 0 AND team_id = ?", teamId);
        jdbcTemplate.update("UPDATE team_message SET deleted = 1 WHERE deleted = 0 AND team_id = ?", teamId);
        jdbcTemplate.update("UPDATE team SET status = 'DISBANDED', deleted = 1 WHERE deleted = 0 AND id = ?", teamId);
    }
}
