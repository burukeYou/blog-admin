package com.myblog.async;

import com.myblog.enums.EventTypeEnum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EventModel implements Serializable {

    //事件类型
    private EventTypeEnum type;

    //事件触发者
    private Long actorId;

    //被触发对象
    private Long entityId;
    private Integer entityType;

    //被触发对象的拥有者
    private Long entityOwnerId;

    //触发现场需要保存下来的数据
    private Map<String,String> exts = new HashMap<>();


    public EventModel() {
    }

    public EventModel(EventTypeEnum type) {
        this.type = type;
    }


    public EventModel setExt(String key,String value){
         exts.put(key,value);
         return this;
    }

    public String getExt(String key){
        return exts.get(key);
    }



    //
    public EventTypeEnum getType() {
        return type;
    }

    public EventModel setType(EventTypeEnum type) {
        this.type = type;
        return this;
    }

    public Long getActorId() {
        return actorId;
    }

    public EventModel setActorId(Long actorId) {
        this.actorId = actorId;
        return this;
    }

    public Long getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(Long entityId) {
        this.entityId = entityId;
        return this;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(Integer entityType) {
        this.entityType = entityType;
        return this;
    }

    public Long getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(Long entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public Map<String,String> setExts(Map<String, String> exts) {
        this.exts = exts;
        return this.exts;
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "type=" + type +
                ", actorId=" + actorId +
                ", entityId=" + entityId +
                ", entityType=" + entityType +
                ", entityOwnerId=" + entityOwnerId +
                ", exts=" + exts +
                '}';
    }
}
