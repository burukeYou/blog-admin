package com.myblog.enums;

public enum  EventTypeEnum {

    LIKE(0),
    UNLIKE(1),
    COMMENT(2),
    EMAIL(3),
    REGISTER(4);

    private Integer type;

    EventTypeEnum(Integer type) {
        this.type = type;
    }


    public Integer getType() {
        return type;
    }
}
