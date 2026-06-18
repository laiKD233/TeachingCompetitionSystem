package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("student")
public class Student {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String studentNo;

    private String realName;

    private String idCard;

    private Integer entryYear;

    private String className;

    private String major;

    private String collegeName;

    private String photo;

    private String bio;

    private Boolean isExternal;

    private String school;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}