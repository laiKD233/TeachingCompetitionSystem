package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("team_task")
public class TeamTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private String title;

    private String description;

    private Long assigneeId;

    private String status;

    private String priority;

    private LocalDateTime dueDate;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String assigneeName;

    @TableField(exist = false)
    private String creatorName;
}
