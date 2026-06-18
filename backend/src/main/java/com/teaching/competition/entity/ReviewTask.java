package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review_task")
public class ReviewTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long competitionId;

    private Long workId;

    private Long reviewerId;

    private Double score;

    private String comment;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
