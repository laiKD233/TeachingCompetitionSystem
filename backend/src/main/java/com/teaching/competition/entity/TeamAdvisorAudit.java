package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("team_advisor_audit")
public class TeamAdvisorAudit {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private Long advisorId;

    private Long requesterId;

    private String status;

    private String reason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String teamName;

    @TableField(exist = false)
    private String advisorName;

    @TableField(exist = false)
    private String requesterName;
}