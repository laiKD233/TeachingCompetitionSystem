package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("todo")
public class Todo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String description;

    private LocalDateTime dueDate;

    private String status;

    private String priority;

    private String type;

    private Long relatedId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
