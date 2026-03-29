package com.teaching.competition.common.enums;

public enum UserRole {
    GUEST("guest", "游客"),
    PARTICIPANT("participant", "参赛人员"),
    COMPETITION_ADMIN("competition_admin", "竞赛管理员"),
    SUPER_ADMIN("super_admin", "超级管理员");

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
