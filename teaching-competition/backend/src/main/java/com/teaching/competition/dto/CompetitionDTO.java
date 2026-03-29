package com.teaching.competition.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompetitionDTO {
    private Long id;
    private String name;
    private String type;
    private String theme;
    private String description;
    private String coverImage;
    private String status;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private LocalDateTime submissionDeadline;
    private LocalDateTime reviewStart;
    private LocalDateTime reviewEnd;
    private LocalDateTime announcementStart;
    private LocalDateTime announcementEnd;
    private String awardsConfig;
    private String rules;
    private String submissionRequirements;
}
