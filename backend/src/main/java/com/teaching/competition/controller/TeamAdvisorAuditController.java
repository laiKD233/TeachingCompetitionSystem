package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.entity.TeamAdvisorAudit;
import com.teaching.competition.service.TeamAdvisorAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team/advisor/audit")
@RequiredArgsConstructor
public class TeamAdvisorAuditController {

    private final TeamAdvisorAuditService auditService;

    /**
     * 发起指导老师审核申请
     */
    @PostMapping("/apply")
    public Result<TeamAdvisorAudit> apply(
            @RequestParam Long teamId,
            @RequestParam Long advisorId,
            @RequestParam(required = false) String reason,
            @RequestAttribute("userId") Long userId) {
        TeamAdvisorAudit audit = auditService.createAudit(teamId, advisorId, userId, reason);
        return Result.success(audit);
    }

    /**
     * 指导老师同意申请
     */
    @PostMapping("/{auditId}/approve")
    public Result<TeamAdvisorAudit> approve(
            @PathVariable Long auditId,
            @RequestAttribute("userId") Long userId) {
        TeamAdvisorAudit audit = auditService.approve(auditId, userId);
        return Result.success(audit);
    }

    /**
     * 指导老师拒绝申请
     */
    @PostMapping("/{auditId}/reject")
    public Result<TeamAdvisorAudit> reject(
            @PathVariable Long auditId,
            @RequestParam(required = false) String reason,
            @RequestAttribute("userId") Long userId) {
        TeamAdvisorAudit audit = auditService.reject(auditId, userId, reason);
        return Result.success(audit);
    }

    /**
     * 获取指导老师待审核的申请列表
     */
    @GetMapping("/pending")
    public Result<List<TeamAdvisorAudit>> getPendingAudits(
            @RequestAttribute("userId") Long userId) {
        List<TeamAdvisorAudit> audits = auditService.getPendingAuditsByAdvisor(userId);
        return Result.success(audits);
    }

    /**
     * 获取团队的所有审核记录
     */
    @GetMapping("/team/{teamId}")
    public Result<List<TeamAdvisorAudit>> getTeamAudits(@PathVariable Long teamId) {
        List<TeamAdvisorAudit> audits = auditService.getAuditsByTeam(teamId);
        return Result.success(audits);
    }

    /**
     * 获取我发起的审核申请
     */
    @GetMapping("/my")
    public Result<List<TeamAdvisorAudit>> getMyAudits(@RequestAttribute("userId") Long userId) {
        List<TeamAdvisorAudit> audits = auditService.getAuditsByRequester(userId);
        return Result.success(audits);
    }
}