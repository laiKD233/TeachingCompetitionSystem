package com.teaching.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationDTO {
    @NotNull(message = "竞赛ID不能为空")
    private Long competitionId;

    @NotBlank(message = "项目名称不能为空")
    private String projectName;
    private String advisor;
    private String description;
}
