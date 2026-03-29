package com.teaching.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkDTO {
    private Long id;
    @NotNull(message = "竞赛ID不能为空")
    private Long competitionId;
    @NotBlank(message = "作品名称不能为空")
    private String title;
    private String description;
}
