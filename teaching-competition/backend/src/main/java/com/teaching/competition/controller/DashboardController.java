package com.teaching.competition.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.competition.common.Result;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.entity.Registration;
import com.teaching.competition.entity.ReviewTask;
import com.teaching.competition.entity.User;
import com.teaching.competition.entity.Work;
import com.teaching.competition.service.CompetitionService;
import com.teaching.competition.service.RegistrationService;
import com.teaching.competition.service.ReviewTaskService;
import com.teaching.competition.service.UserService;
import com.teaching.competition.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CompetitionService competitionService;
    private final RegistrationService registrationService;
    private final ReviewTaskService reviewTaskService;
    private final WorkService workService;
    private final UserService userService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCompetitions", competitionService.count());
        stats.put("totalRegistrations", registrationService.count());
        stats.put("pendingRegistrations", registrationService.count(
                new LambdaQueryWrapper<Registration>().eq(Registration::getStatus, "PENDING")));
        stats.put("pendingReviews", reviewTaskService.count(
                new LambdaQueryWrapper<ReviewTask>().eq(ReviewTask::getStatus, "PENDING")));
        stats.put("totalWorks", workService.count());
        stats.put("totalUsers", userService.count());

        return Result.success(stats);
    }
}
