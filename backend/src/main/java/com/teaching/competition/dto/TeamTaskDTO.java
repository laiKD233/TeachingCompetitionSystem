package com.teaching.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamTaskDTO {

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 200, message = "任务标题最多200个字符")
    private String title;

    private String description;

    private Long assigneeId;

    private String priority = "MEDIUM";

    private LocalDateTime dueDate;
}
