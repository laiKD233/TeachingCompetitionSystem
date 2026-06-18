package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("team")
public class Team {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private Long competitionId;

    private Long leaderId;

    private Long advisorId;

    private Integer maxMembers;

    private String status;

    private String inviteCode;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String leaderName;

    @TableField(exist = false)
    private String advisorName;

    @TableField(exist = false)
    private List<String> advisorNames;

    @TableField(exist = false)
    private List<Long> advisorIds;

    @TableField(exist = false)
    private String competitionName;

    @TableField(exist = false)
    private Integer memberCount;
}
