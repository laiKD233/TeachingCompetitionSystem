package com.teaching.competition.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewTaskVO {
    private Long id;
    private Long competitionId;
    private String competitionName;
    private Long workId;
    private String workTitle;
    private String workFileUrl;
    private String workFileName;
    private Long reviewerId;
    private String reviewerName;
    private String participantName;
    private Double score;
    private String comment;
    private String status;
    private LocalDateTime createdAt;
}
