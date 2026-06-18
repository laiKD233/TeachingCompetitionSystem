package com.teaching.competition.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String name;
    private String college;
    private String className;
    private String phone;
    private String email;
    private String avatar;
    private String bio;
}
