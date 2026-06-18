package com.teaching.competition.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeacherDTO {

    @NotNull(message = "工号不能为空")
    private String teacherNo;

    @NotNull(message = "真实姓名不能为空")
    private String realName;

    private String idCard;

    private Integer education;

    private Integer degree;

    private String bio;

    private String major;

    private Boolean isExternal;

    private String collegeName;

    private String school;
}