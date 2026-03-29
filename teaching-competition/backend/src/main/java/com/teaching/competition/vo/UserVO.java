package com.teaching.competition.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String name;
    private String studentId;
    private String college;
    private String className;
    private String phone;
    private String email;
    private String avatar;
    private String bio;
    private String role;
    private Integer status;
    private String permissions;
    private LocalDateTime createdAt;
}
