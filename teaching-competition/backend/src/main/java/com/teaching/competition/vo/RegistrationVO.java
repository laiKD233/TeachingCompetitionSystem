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
    private String description;
    private String status;
    private String rejectReason;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    // 关联的竞赛信息
    private String competitionName;

    // 关联的用户信息
    private String participantName;
    private String studentId;
    private String college;
}
