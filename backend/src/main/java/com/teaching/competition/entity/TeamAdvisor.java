package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("team_advisor")
public class TeamAdvisor {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private Long advisorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String advisorName;
}
