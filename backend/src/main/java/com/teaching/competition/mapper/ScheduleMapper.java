package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    @Select("SELECT * FROM schedule WHERE user_id = #{userId} AND deleted = 0 " +
            "AND start_time >= #{startDate} AND start_time < #{endDate} " +
            "ORDER BY start_time ASC")
    List<Schedule> selectByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Select("SELECT COUNT(1) FROM schedule " +
            "WHERE user_id = #{userId} AND deleted = 0 " +
            "AND start_time <= #{startTime} AND end_time >= #{endTime}")
    int countCoveringRange(@Param("userId") Long userId,
                           @Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);
}
