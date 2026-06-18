package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.AppointmentDTO;
import com.teaching.competition.entity.Appointment;
import com.teaching.competition.entity.User;
import com.teaching.competition.mapper.AppointmentMapper;
import com.teaching.competition.mapper.ScheduleMapper;
import com.teaching.competition.mapper.UserMapper;
import com.teaching.competition.service.AppointmentService;
import com.teaching.competition.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    private final AppointmentMapper appointmentMapper;
    private final ScheduleMapper scheduleMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Appointment createAppointment(AppointmentDTO dto, Long studentId) {
        User advisor = userMapper.selectById(dto.getAdvisorId());
        if (advisor == null || advisor.getStatus() == null || advisor.getStatus() != 1) {
            throw new RuntimeException("指导老师不存在或不可用");
        }

        int duration = dto.getDuration() != null ? dto.getDuration() : 60;
        if (duration <= 0) {
            throw new RuntimeException("预约时长必须大于0");
        }

        LocalDateTime startTime = dto.getAppointmentDate();
        LocalDateTime endTime = startTime.plusMinutes(duration);

        int scheduleCoverage = scheduleMapper.countCoveringRange(dto.getAdvisorId(), startTime, endTime);
        if (scheduleCoverage <= 0) {
            throw new RuntimeException("预约时间不在老师日程安排内，请选择老师空闲时段");
        }

        if (appointmentMapper.countAdvisorTimeConflict(dto.getAdvisorId(), startTime, endTime) > 0) {
            throw new RuntimeException("该时间段老师已有预约，请选择其他时段");
        }

        if (appointmentMapper.countStudentTimeConflict(studentId, startTime, endTime) > 0) {
            throw new RuntimeException("你在该时间段已有预约，请勿重复预约");
        }

        Appointment appointment = new Appointment();
        appointment.setStudentId(studentId);
        appointment.setAdvisorId(dto.getAdvisorId());
        appointment.setTitle(dto.getTitle());
        appointment.setDescription(dto.getDescription());
        appointment.setAppointmentDate(startTime);
        appointment.setDuration(duration);
        appointment.setLocation(dto.getLocation());
        appointment.setStatus("PENDING");
        save(appointment);

        notificationService.sendNotification(dto.getAdvisorId(),
                "新的预约申请",
                "学生预约了您的指导：「" + dto.getTitle() + "」",
                "APPOINTMENT", appointment.getId());
        return appointment;
    }

    @Override
    @Transactional
    public void approveAppointment(Long id, Long advisorId) {
        Appointment appointment = getById(id);
        if (appointment == null || (advisorId != null && !appointment.getAdvisorId().equals(advisorId))) {
            throw new RuntimeException("预约不存在或无权限");
        }
        appointment.setStatus("APPROVED");
        updateById(appointment);

        notificationService.sendNotification(appointment.getStudentId(),
                "预约已通过",
                "您的预约「" + appointment.getTitle() + "」已被指导老师批准",
                "APPOINTMENT", appointment.getId());
    }

    @Override
    @Transactional
    public void rejectAppointment(Long id, String reason, Long advisorId) {
        Appointment appointment = getById(id);
        if (appointment == null || (advisorId != null && !appointment.getAdvisorId().equals(advisorId))) {
            throw new RuntimeException("预约不存在或无权限");
        }
        appointment.setStatus("REJECTED");
        appointment.setRejectReason(reason);
        updateById(appointment);

        notificationService.sendNotification(appointment.getStudentId(),
                "预约被拒绝",
                "您的预约「" + appointment.getTitle() + "」被指导老师拒绝，原因：" + reason,
                "APPOINTMENT", appointment.getId());
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id, Long userId) {
        Appointment appointment = getById(id);
        if (appointment == null || !appointment.getStudentId().equals(userId)) {
            throw new RuntimeException("预约不存在或无权限");
        }
        appointment.setStatus("CANCELLED");
        updateById(appointment);
    }

    @Override
    @Transactional
    public void completeAppointment(Long id, Long advisorId) {
        Appointment appointment = getById(id);
        if (appointment == null || (advisorId != null && !appointment.getAdvisorId().equals(advisorId))) {
            throw new RuntimeException("预约不存在或无权限");
        }
        appointment.setStatus("COMPLETED");
        updateById(appointment);
    }

    @Override
    public List<Appointment> getMyAppointments(Long userId, String role) {
        if ("STUDENT".equals(role)) {
            return appointmentMapper.selectByStudentId(userId);
        } else if ("ADMIN".equals(role)) {
            return appointmentMapper.selectAllWithStatus(null);
        } else {
            return appointmentMapper.selectByAdvisorId(userId);
        }
    }

    @Override
    public List<Appointment> getAppointmentsByAdvisor(Long advisorId, String status) {
        return appointmentMapper.selectByAdvisorIdAndStatus(advisorId, status);
    }
}
