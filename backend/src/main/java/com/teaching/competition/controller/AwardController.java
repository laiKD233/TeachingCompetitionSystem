package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.AwardDTO;
import com.teaching.competition.entity.Award;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.AwardService;
import com.teaching.competition.service.CompetitionAdminService;
import com.teaching.competition.service.CompetitionService;
import com.teaching.competition.vo.ScoreVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/award")
@RequiredArgsConstructor
public class AwardController {

    private final AwardService awardService;
    private final CompetitionService competitionService;
    private final CompetitionAdminService competitionAdminService;

    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    @PostMapping("/publish")
    public Result<Void> publishAwards(@RequestBody @Valid AwardDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (!canManageAwards(user, dto.getCompetitionId())) {
            return Result.error(403, "无权发布该竞赛奖项");
        }
        awardService.publishAwards(dto);
        return Result.success();
    }

    @PostMapping("/announcement/{competitionId}")
    public Result<Void> publishAnnouncement(@PathVariable Long competitionId, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (!canManageAwards(user, competitionId)) {
            return Result.error(403, "无权公示该竞赛成绩");
        }
        awardService.publishAnnouncement(competitionId);
        return Result.success();
    }

    @GetMapping("/list/{competitionId}")
    public Result<List<Award>> getAwards(@PathVariable Long competitionId, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (!canViewManagedCompetition(user, competitionId)) {
            return Result.error(403, "无权查看该竞赛奖项");
        }
        List<Award> awards = awardService.getAwardsByCompetition(competitionId);
        return Result.success(awards);
    }

    @GetMapping("/results/{competitionId}")
    public Result<List<ScoreVO>> getAwardResults(
            @PathVariable Long competitionId,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Competition competition = competitionService.getById(competitionId);
        if (competition == null) {
            return Result.error("竞赛不存在");
        }
        Long visibleUserId = "STUDENT".equals(user.getRole()) ? user.getId() : null;
        if (visibleUserId != null && !isResultVisible(competition.getStatus())) {
            return Result.error(403, "该竞赛成绩尚未公示");
        }
        if (visibleUserId == null && !canViewManagedCompetition(user, competitionId)) {
            return Result.error(403, "无权查看该竞赛成绩");
        }
        List<ScoreVO> results = awardService.getAwardResults(competitionId, visibleUserId);
        return Result.success(results);
    }

    @GetMapping("/public/results/{competitionId}")
    @PreAuthorize("permitAll()")
    public Result<List<ScoreVO>> getPublicResults(@PathVariable Long competitionId) {
        Competition competition = competitionService.getById(competitionId);
        if (competition == null) {
            return Result.error("竞赛不存在");
        }
        if (!isResultVisible(competition.getStatus())) {
            return Result.error(403, "该竞赛成绩尚未公示");
        }
        List<ScoreVO> results = awardService.getAwardResults(competitionId, null);
        return Result.success(results);
    }

    @GetMapping("/finished/{competitionId}")
    public Result<List<Award>> getFinishedAwards(@PathVariable Long competitionId) {
        List<Award> awards = awardService.getFinishedAwards(competitionId);
        return Result.success(awards);
    }

    private boolean canManageAwards(User user, Long competitionId) {
        if (user == null || competitionId == null) {
            return false;
        }
        if (!"ADMIN".equals(user.getRole()) && !"TEACHER".equals(user.getRole())) {
            return false;
        }
        if ("ADMIN".equals(user.getRole())) {
            return true;
        }
        return competitionAdminService.hasCompetitionPermission(user.getId(), competitionId);
    }

    private boolean canViewManagedCompetition(User user, Long competitionId) {
        if (user == null || competitionId == null) {
            return false;
        }
        if (!"ADMIN".equals(user.getRole()) && !"TEACHER".equals(user.getRole()) && !"ADVISOR".equals(user.getRole())) {
            return false;
        }
        if ("ADMIN".equals(user.getRole())) {
            return true;
        }
        return competitionAdminService.hasCompetitionPermission(user.getId(), competitionId);
    }

    private boolean isResultVisible(String status) {
        return "ANNOUNCEMENT".equals(status) || "FINISHED".equals(status)
                || "ANNOUNCED".equals(status) || "ENDED".equals(status);
    }
}
