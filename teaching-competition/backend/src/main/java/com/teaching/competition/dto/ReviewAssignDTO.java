package com.teaching.competition.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ReviewAssignDTO {
    private Long competitionId;
    private String assignType;
    private List<Long> reviewerIds;
    private Integer worksPerReviewer;
    private List<ReviewItemDTO> items;

    @Data
    public static class ReviewItemDTO {
        private Long workId;
        private Long reviewerId;
    }
}
