package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("schedule")
public class Schedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private String type;

    private Long relatedId;

    private String color;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
