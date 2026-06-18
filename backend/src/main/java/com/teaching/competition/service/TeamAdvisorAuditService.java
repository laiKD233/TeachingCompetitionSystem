package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.entity.TeamAdvisorAudit;

import java.util.List;

public interface TeamAdvisorAuditService extends IService<TeamAdvisorAudit> {

    TeamAdvisorAudit createAudit(Long teamId, Long advisorId, Long requesterId, String reason);

    TeamAdvisorAudit approve(Long auditId, Long advisorId);

    TeamAdvisorAudit reject(Long auditId, Long advisorId, String rejectReason);

    List<TeamAdvisorAudit> getPendingAuditsByAdvisor(Long advisorId);

    List<TeamAdvisorAudit> getAuditsByTeam(Long teamId);

    List<TeamAdvisorAudit> getAuditsByRequester(Long requesterId);

    boolean hasPendingAudit(Long teamId, Long advisorId);
}