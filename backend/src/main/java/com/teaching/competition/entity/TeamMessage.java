package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("team_message")
public class TeamMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private Long userId;

    private String content;

    private String type;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String userAvatar;
}
