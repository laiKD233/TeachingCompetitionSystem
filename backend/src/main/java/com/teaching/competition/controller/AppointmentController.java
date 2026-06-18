package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.AppointmentDTO;
import com.teaching.competition.entity.Appointment;
import com.teaching.competition.entity.Schedule;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.AppointmentService;
import com.teaching.competition.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ScheduleService scheduleService;

    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @PostMapping
    public Result<Appointment> createAppointment(@RequestBody @Valid AppointmentDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Appointment appointment = appointmentService.createAppointment(dto, user.getId());
        return Result.success(appointment);
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approveAppointment(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        appointmentService.approveAppointment(id, "ADMIN".equals(user.getRole()) ? null : user.getId());
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    public Result<Void> rejectAppointment(@PathVariable Long id, @RequestParam String reason, Authentication authentication) {
        User user = getCurrentUser(authentication);
        appointmentService.rejectAppointment(id, reason, "ADMIN".equals(user.getRole()) ? null : user.getId());
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelAppointment(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        appointmentService.cancelAppointment(id, user.getId());
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    public Result<Void> completeAppointment(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        appointmentService.completeAppointment(id, "ADMIN".equals(user.getRole()) ? null : user.getId());
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<Appointment>> getMyAppointments(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Appointment> appointments = appointmentService.getMyAppointments(user.getId(), user.getRole());
        return Result.success(appointments);
    }

    @GetMapping("/advisor")
    public Result<List<Appointment>> getAdvisorAppointments(@RequestParam(required = false) String status, Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Appointment> appointments = "ADMIN".equals(user.getRole())
                ? appointmentService.getMyAppointments(user.getId(), user.getRole()).stream()
                    .filter(item -> status == null || status.isBlank() || status.equals(item.getStatus()))
                    .toList()
                : appointmentService.getAppointmentsByAdvisor(user.getId(), status);
        return Result.success(appointments);
    }

    @GetMapping("/advisor/{advisorId}/schedules")
    public Result<List<Schedule>> getAdvisorMonthSchedules(@PathVariable Long advisorId,
                                                           @RequestParam int year,
                                                           @RequestParam int month) {
        List<Schedule> schedules = scheduleService.getMonthSchedules(advisorId, year, month);
        return Result.success(schedules);
    }
}
