package com.teaching.competition.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewScoreDTO {
    @NotNull(message = "评审任务ID不能为空")
    private Long taskId;

    @NotNull(message = "评分不能为空")
    @Min(value = 0, message = "分数不能小于0")
    @Max(value = 100, message = "分数不能大于100")
    private Double score;
    private String comment;
}
