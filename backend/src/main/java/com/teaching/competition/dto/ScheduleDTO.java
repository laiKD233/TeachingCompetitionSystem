package com.teaching.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleDTO {

    @NotBlank(message = "日程标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    private String location;

    private String color;
}
