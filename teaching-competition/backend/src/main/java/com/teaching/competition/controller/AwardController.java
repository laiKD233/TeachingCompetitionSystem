package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.AwardDTO;
import com.teaching.competition.entity.Award;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.AwardService;
import com.teaching.competition.vo.ScoreVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/award")
@RequiredArgsConstructor
public class AwardController {

    private final AwardService awardService;

    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    @PostMapping("/publish")
    public Result<Void> publishAwards(@RequestBody @Valid AwardDTO dto) {
        awardService.publishAwards(dto);
        return Result.success();
    }

    @PostMapping("/announcement/{competitionId}")
    public Result<Void> publishAnnouncement(@PathVariable Long competitionId) {
        awardService.publishAnnouncement(competitionId);
        return Result.success();
    }

    @GetMapping("/list/{competitionId}")
    public Result<List<Award>> getAwards(@PathVariable Long competitionId) {
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
        List<ScoreVO> results = awardService.getAwardResults(competitionId, user.getId());
        return Result.success(results);
    }

    @GetMapping("/finished/{competitionId}")
    public Result<List<Award>> getFinishedAwards(@PathVariable Long competitionId) {
        List<Award> awards = awardService.getFinishedAwards(competitionId);
        return Result.success(awards);
    }
}
