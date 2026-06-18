package com.teaching.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {

    @NotNull(message = "指导老师不能为空")
    private Long advisorId;

    @NotBlank(message = "预约主题不能为空")
    private String title;

    private String description;

    @NotNull(message = "预约时间不能为空")
    private LocalDateTime appointmentDate;

    private Integer duration = 60;

    private String location;
}
