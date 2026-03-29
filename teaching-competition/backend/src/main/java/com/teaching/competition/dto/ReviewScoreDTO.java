package com.teaching.competition.dto;

import lombok.Data;

@Data
public class ReviewScoreDTO {
    private Long taskId;
    private Double score;
    private String comment;
}
