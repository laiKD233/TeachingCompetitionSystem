package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("competition")
public class Competition {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String type;

    private String theme;

    private String description;

    private String coverImage;

    private String status;

    private LocalDateTime registrationStart;

    private LocalDateTime registrationEnd;

    private LocalDateTime submissionDeadline;

    private LocalDateTime reviewStart;

    private LocalDateTime reviewEnd;

    private LocalDateTime announcementStart;

    private LocalDateTime announcementEnd;

    private String awardsConfig;

    private String rules;

    private String submissionRequirements;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
