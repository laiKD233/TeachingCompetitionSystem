package com.teaching.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationDTO {
    @NotNull(message = "竞赛ID不能为空")
    private Long competitionId;
    @NotNull(message = "参赛方式不能为空")
    private String participationType;

    private Long teamId;

    private String projectName;
    private Long advisorId;
    private String advisor;
    private String description;

    private Long trackId;
}
