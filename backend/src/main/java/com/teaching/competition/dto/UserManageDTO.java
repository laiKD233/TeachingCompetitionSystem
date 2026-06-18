package com.teaching.competition.dto;

import lombok.Data;

@Data
public class UserManageDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private Integer status;
    private String permissions;
    private String studentId;
    private String college;
    private String className;
    private String phone;
    private String email;
}
