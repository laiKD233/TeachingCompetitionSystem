package com.teaching.competition.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegistrationVO {
    private Long id;
    private Long competitionId;
    private Long userId;
    private String projectName;
    private String advisor;
    private String participationType;
    private Long teamId;
    private String description;
    private String status;
    private String rejectReason;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    private String competitionName;
    private String competitionStatus;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private LocalDateTime submissionDeadline;

    private String participantName;
    private String studentId;
    private String college;

    private String teamName;

    private Long trackId;
    private String trackName;
}
