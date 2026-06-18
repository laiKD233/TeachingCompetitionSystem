package com.teaching.competition.common.enums;

public enum RegistrationStatus {
    PENDING("pending", "待审核"),
    APPROVED("approved", "审核通过"),
    REJECTED("rejected", "已驳回");

    private final String code;
    private final String desc;

    RegistrationStatus(String code, String desc) {
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
