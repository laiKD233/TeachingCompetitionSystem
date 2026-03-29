package com.teaching.competition.common.enums;

public enum CompetitionStatus {
    DRAFT("draft", "草稿"),
    NOT_STARTED("not_started", "未开始"),
    REGISTRATION("registration", "报名中"),
    SUBMISSION("submission", "作品提交中"),
    REVIEW("review", "评审中"),
    ANNOUNCEMENT("announcement", "公示中"),
    FINISHED("finished", "已结束");

    private final String code;
    private final String desc;

    CompetitionStatus(String code, String desc) {
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
