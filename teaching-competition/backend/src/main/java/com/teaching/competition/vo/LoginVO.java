package com.teaching.competition.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String name;
    private String role;
}
