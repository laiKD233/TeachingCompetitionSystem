package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("award")
public class Award {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long competitionId;

    private Long userId;

    private Long workId;

    private String awardLevel;

    private String certificateUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;
}
