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

    // 兼容前端“单个作品+评审人”的简化调用
    // 如果前端直接传 { workId, reviewerId, competitionId }，
    // 服务层会在 items 为空时基于这两个字段构造单条分配记录。
    private Long workId;
    private Long reviewerId;

    @Data
    public static class ReviewItemDTO {
        private Long workId;
        private Long reviewerId;
    }
}
