package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.dto.AppointmentDTO;
import com.teaching.competition.entity.Appointment;

import java.util.List;

public interface AppointmentService extends IService<Appointment> {
    
    Appointment createAppointment(AppointmentDTO dto, Long studentId);
    
    void approveAppointment(Long id, Long advisorId);
    
    void rejectAppointment(Long id, String reason, Long advisorId);
    
    void cancelAppointment(Long id, Long userId);
    
    void completeAppointment(Long id, Long advisorId);
    
    List<Appointment> getMyAppointments(Long userId, String role);
    
    List<Appointment> getAppointmentsByAdvisor(Long advisorId, String status);
}
