package com.myblog.enums;

/**
 *  多个枚举属性间用'，'隔开
 */
public enum  NotificationTypeEnum {

    COMMENT_BLOG(1,"评论了博客消息"),
    REPLY_COMMENT(2,"回复消息"),

    PRAISE(3,"点赞消息");
    //PRAISE_COMMENT(4,"点赞了评论");

    private int type;
    private String name;


    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    NotificationTypeEnum(int status, String name) {
        this.type = status;
        this.name = name;
    }


    //根据type获取name
    public static String nameOfType(int type){
        for (NotificationTypeEnum e : NotificationTypeEnum.values()) {
            if (e.getType() == type)
                return e.getName();
        }

        return "";
    }



}
