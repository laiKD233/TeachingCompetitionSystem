package com.teaching.competition.common.enums;

public enum UserRole {
    STUDENT("STUDENT", "参赛人员"),
    TEACHER("TEACHER", "竞赛管理员"),
    ADMIN("ADMIN", "超级管理员"),
    ADVISOR("ADVISOR", "指导老师");

    private final String code;
    private final String desc;

    UserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
