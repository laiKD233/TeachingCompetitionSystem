package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("registration")
public class Registration {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long competitionId;

    private Long userId;

    private String projectName;

    private String advisor;

    private String description;

    private String status;

    private String rejectReason;

    private Long reviewedBy;

    private LocalDateTime reviewedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
