package com.teaching.competition.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentDTO {

    @NotNull(message = "学号不能为空")
    private String studentNo;

    @NotNull(message = "真实姓名不能为空")
    private String realName;

    private String idCard;

    private Integer entryYear;

    private String className;

    private String major;

    private String collegeName;

    private String bio;

    private Boolean isExternal;

    private String school;
}