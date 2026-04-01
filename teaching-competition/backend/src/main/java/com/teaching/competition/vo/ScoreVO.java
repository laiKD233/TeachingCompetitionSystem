package com.teaching.competition.vo;

import lombok.Data;

@Data
public class ScoreVO {
    private Long workId;
    private String workTitle;
    private String participantName;
    private Double avgScore;
    private String rank;
    private String awardLevel;
    private String reviewComment;
    private String fileUrl;
    private String fileName;
}
