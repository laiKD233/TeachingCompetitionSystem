package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("teacher")
public class Teacher {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String realName;

    private String teacherNo;

    private Integer education;

    private Integer degree;

    private String bio;

    private String major;

    private String idCard;

    private String photo;

    private Boolean isExternal;

    private String collegeName;

    private String school;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}