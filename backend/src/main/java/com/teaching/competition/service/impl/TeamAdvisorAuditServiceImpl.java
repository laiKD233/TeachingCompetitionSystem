package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.entity.Team;
import com.teaching.competition.entity.TeamAdvisor;
import com.teaching.competition.entity.TeamAdvisorAudit;
import com.teaching.competition.entity.User;
import com.teaching.competition.mapper.TeamAdvisorAuditMapper;
import com.teaching.competition.mapper.TeamAdvisorMapper;
import com.teaching.competition.mapper.TeamMapper;
import com.teaching.competition.mapper.UserMapper;
import com.teaching.competition.service.TeamAdvisorAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamAdvisorAuditServiceImpl extends ServiceImpl<TeamAdvisorAuditMapper, TeamAdvisorAudit> 
        implements TeamAdvisorAuditService {

    private final TeamAdvisorAuditMapper auditMapper;
    private final TeamAdvisorMapper teamAdvisorMapper;
    private final TeamMapper teamMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public TeamAdvisorAudit createAudit(Long teamId, Long advisorId, Long requesterId, String reason) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }
        User advisor = userMapper.selectById(advisorId);
        if (advisor == null || advisor.getStatus() == null || advisor.getStatus() != 1
                || (!"TEACHER".equals(advisor.getRole()) && !"ADVISOR".equals(advisor.getRole()))) {
            throw new RuntimeException("请选择系统中存在且启用的指导老师");
        }

        if (hasPendingAudit(teamId, advisorId)) {
            throw new RuntimeException("该指导老师已有待审核的申请");
        }

        LambdaQueryWrapper<TeamAdvisor> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(TeamAdvisor::getTeamId, teamId);
        checkWrapper.eq(TeamAdvisor::getAdvisorId, advisorId);
        if (teamAdvisorMapper.selectCount(checkWrapper) > 0) {
            throw new RuntimeException("该指导老师已加入团队");
        }

        TeamAdvisorAudit audit = new TeamAdvisorAudit();
        audit.setTeamId(teamId);
        audit.setAdvisorId(advisorId);
        audit.setRequesterId(requesterId);
        audit.setStatus("PENDING");
        audit.setReason(reason);
        audit.setCreatedAt(LocalDateTime.now());

        auditMapper.insert(audit);
        return getAuditDetail(audit.getId());
    }

    @Override
    @Transactional
    public TeamAdvisorAudit approve(Long auditId, Long advisorId) {
        TeamAdvisorAudit audit = auditMapper.selectById(auditId);
        if (audit == null) {
            throw new RuntimeException("审核记录不存在");
        }
        if (!audit.getAdvisorId().equals(advisorId)) {
            throw new RuntimeException("只能审核自己的申请");
        }
        if (!"PENDING".equals(audit.getStatus())) {
            throw new RuntimeException("申请状态不允许审核");
        }

        audit.setStatus("APPROVED");
        audit.setReviewedAt(LocalDateTime.now());
        auditMapper.updateById(audit);

        TeamAdvisor teamAdvisor = new TeamAdvisor();
        teamAdvisor.setTeamId(audit.getTeamId());
        teamAdvisor.setAdvisorId(audit.getAdvisorId());
        teamAdvisorMapper.insert(teamAdvisor);

        return getAuditDetail(auditId);
    }

    @Override
    @Transactional
    public TeamAdvisorAudit reject(Long auditId, Long advisorId, String rejectReason) {
        TeamAdvisorAudit audit = auditMapper.selectById(auditId);
        if (audit == null) {
            throw new RuntimeException("审核记录不存在");
        }
        if (!audit.getAdvisorId().equals(advisorId)) {
            throw new RuntimeException("只能审核自己的申请");
        }
        if (!"PENDING".equals(audit.getStatus())) {
            throw new RuntimeException("申请状态不允许审核");
        }

        audit.setStatus("REJECTED");
        audit.setReason(rejectReason);
        audit.setReviewedAt(LocalDateTime.now());
        auditMapper.updateById(audit);

        return getAuditDetail(auditId);
    }

    @Override
    public List<TeamAdvisorAudit> getPendingAuditsByAdvisor(Long advisorId) {
        LambdaQueryWrapper<TeamAdvisorAudit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamAdvisorAudit::getAdvisorId, advisorId);
        wrapper.eq(TeamAdvisorAudit::getStatus, "PENDING");
        wrapper.orderByDesc(TeamAdvisorAudit::getCreatedAt);
        List<TeamAdvisorAudit> audits = auditMapper.selectList(wrapper);
        audits.forEach(this::fillAuditDetail);
        return audits;
    }

    @Override
    public List<TeamAdvisorAudit> getAuditsByTeam(Long teamId) {
        List<TeamAdvisorAudit> audits = auditMapper.selectByTeamId(teamId);
        audits.forEach(this::fillAuditDetail);
        return audits;
    }

    @Override
    public List<TeamAdvisorAudit> getAuditsByRequester(Long requesterId) {
        List<TeamAdvisorAudit> audits = auditMapper.selectByRequesterId(requesterId);
        audits.forEach(this::fillAuditDetail);
        return audits;
    }

    @Override
    public boolean hasPendingAudit(Long teamId, Long advisorId) {
        LambdaQueryWrapper<TeamAdvisorAudit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamAdvisorAudit::getTeamId, teamId);
        wrapper.eq(TeamAdvisorAudit::getAdvisorId, advisorId);
        wrapper.eq(TeamAdvisorAudit::getStatus, "PENDING");
        return auditMapper.selectCount(wrapper) > 0;
    }

    private TeamAdvisorAudit getAuditDetail(Long auditId) {
        TeamAdvisorAudit audit = auditMapper.selectById(auditId);
        if (audit != null) {
            fillAuditDetail(audit);
        }
        return audit;
    }

    private void fillAuditDetail(TeamAdvisorAudit audit) {
        if (audit.getTeamId() != null) {
            Team team = teamMapper.selectById(audit.getTeamId());
            if (team != null) {
                audit.setTeamName(team.getName());
            }
        }
        if (audit.getAdvisorId() != null) {
            User advisor = userMapper.selectById(audit.getAdvisorId());
            if (advisor != null) {
                audit.setAdvisorName(advisor.getName());
            }
        }
        if (audit.getRequesterId() != null) {
            User requester = userMapper.selectById(audit.getRequesterId());
            if (requester != null) {
                audit.setRequesterName(requester.getName());
            }
        }
    }
}
