package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("team_member")
public class TeamMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private Long userId;

    private String role;

    private LocalDateTime joinTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String userAvatar;
}
