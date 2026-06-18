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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/user-trend")
    public Result<Map<String, Object>> getUserTrend(@RequestParam(defaultValue = "7") int days) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.toString());
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            long count = userService.count(new LambdaQueryWrapper<User>()
                    .ge(User::getCreatedAt, start)
                    .le(User::getCreatedAt, end));
            counts.add(count);
        }

        result.put("dates", dates);
        result.put("counts", counts);
        return Result.success(result);
    }

    @GetMapping("/competition-stats")
    public Result<Map<String, Object>> getCompetitionStats() {
        Map<String, Object> result = new HashMap<>();

        List<Competition> allCompetitions = competitionService.list();
        Map<String, Long> statusCount = allCompetitions.stream()
                .collect(Collectors.groupingBy(c -> c.getStatus() != null ? c.getStatus() : "UNKNOWN", Collectors.counting()));

        Map<String, Long> typeCount = allCompetitions.stream()
                .collect(Collectors.groupingBy(c -> c.getType() != null ? c.getType() : "未分类", Collectors.counting()));

        List<Map<String, Object>> statusData = statusCount.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", translateStatus(e.getKey()));
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> typeData = typeCount.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", e.getKey());
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        result.put("statusData", statusData);
        result.put("typeData", typeData);
        return Result.success(result);
    }

    private String translateStatus(String status) {
        switch (status) {
            case "DRAFT": return "未发布";
            case "PUBLISHED": return "已发布";
            case "REGISTRATION": return "报名中";
            case "SUBMISSION": return "提交中";
            case "REVIEW": return "评审中";
            case "ANNOUNCEMENT": return "公示中";
            case "ONGOING": return "进行中";
            case "REVIEWED": return "已评审";
            case "ANNOUNCED": return "公示中";
            case "ENDED": return "已结束";
            case "FINISHED": return "已结束";
            default: return status;
        }
    }
}
