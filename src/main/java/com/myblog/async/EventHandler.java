package com.myblog.async;

import com.myblog.enums.EventTypeEnum;

import java.util.List;

public interface EventHandler {

    //根据事件模型处理事件
    void doHandle(EventModel eventModel);

    //需要处理的或者说支持的事件类型
    List<EventTypeEnum> getSupportEvenTypes();


}
