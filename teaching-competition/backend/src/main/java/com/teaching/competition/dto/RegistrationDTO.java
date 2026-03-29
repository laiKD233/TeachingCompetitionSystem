package com.teaching.competition.dto;

import lombok.Data;

@Data
public class RegistrationDTO {
    private Long competitionId;
    private String projectName;
    private String advisor;
    private String description;
}
