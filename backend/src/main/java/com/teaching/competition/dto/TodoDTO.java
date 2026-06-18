package com.teaching.competition.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoDTO {

    @NotBlank(message = "待办标题不能为空")
    private String title;

    private String description;

    private LocalDateTime dueDate;

    private String priority = "MEDIUM";
}
