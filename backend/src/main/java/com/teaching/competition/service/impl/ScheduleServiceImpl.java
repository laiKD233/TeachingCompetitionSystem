package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.ScheduleDTO;
import com.teaching.competition.entity.Schedule;
import com.teaching.competition.mapper.ScheduleMapper;
import com.teaching.competition.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    private final ScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public Schedule createSchedule(ScheduleDTO dto, Long userId) {
        Schedule schedule = new Schedule();
        schedule.setUserId(userId);
        schedule.setTitle(dto.getTitle());
        schedule.setDescription(dto.getDescription());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setLocation(dto.getLocation());
        schedule.setType("PERSONAL");
        schedule.setColor(dto.getColor());
        save(schedule);
        return schedule;
    }

    @Override
    @Transactional
    public void updateSchedule(Long id, ScheduleDTO dto, Long userId) {
        Schedule schedule = getById(id);
        if (schedule == null || !schedule.getUserId().equals(userId)) {
            throw new RuntimeException("日程不存在或无权限");
        }
        schedule.setTitle(dto.getTitle());
        schedule.setDescription(dto.getDescription());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setLocation(dto.getLocation());
        schedule.setColor(dto.getColor());
        updateById(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long id, Long userId) {
        Schedule schedule = getById(id);
        if (schedule == null || !schedule.getUserId().equals(userId)) {
            throw new RuntimeException("日程不存在或无权限");
        }
        removeById(id);
    }

    @Override
    public List<Schedule> getSchedulesByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleMapper.selectByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public List<Schedule> getMonthSchedules(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return scheduleMapper.selectByUserIdAndDateRange(userId, startDate, endDate);
    }
}
