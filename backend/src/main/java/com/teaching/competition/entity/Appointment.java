package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("appointment")
public class Appointment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long advisorId;

    private String title;

    private String description;

    private LocalDateTime appointmentDate;

    private Integer duration;

    private String location;

    private String status;

    private String rejectReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String studentName;

    @TableField(exist = false)
    private String advisorName;
}
