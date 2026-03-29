package com.teaching.competition.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "学号/工号不能为空")
    private String studentId;

    @NotBlank(message = "所属学院不能为空")
    private String college;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "登录账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
