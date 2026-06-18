package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.dto.ScheduleDTO;
import com.teaching.competition.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService extends IService<Schedule> {
    
    Schedule createSchedule(ScheduleDTO dto, Long userId);
    
    void updateSchedule(Long id, ScheduleDTO dto, Long userId);
    
    void deleteSchedule(Long id, Long userId);
    
    List<Schedule> getSchedulesByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Schedule> getMonthSchedules(Long userId, int year, int month);
}
