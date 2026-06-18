package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.ScheduleDTO;
import com.teaching.competition.entity.Schedule;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @PostMapping
    public Result<Schedule> createSchedule(@RequestBody @Valid ScheduleDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Schedule schedule = scheduleService.createSchedule(dto, user.getId());
        return Result.success(schedule);
    }

    @PutMapping("/{id}")
    public Result<Void> updateSchedule(@PathVariable Long id, @RequestBody @Valid ScheduleDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        scheduleService.updateSchedule(id, dto, user.getId());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSchedule(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        scheduleService.deleteSchedule(id, user.getId());
        return Result.success();
    }

    @GetMapping("/month")
    public Result<List<Schedule>> getMonthSchedules(@RequestParam int year, @RequestParam int month, Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Schedule> schedules = scheduleService.getMonthSchedules(user.getId(), year, month);
        return Result.success(schedules);
    }
}
