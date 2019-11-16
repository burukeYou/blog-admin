package com.myblog.async;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    @Autowired
    private RedisTemplate redisTemplate;

    //发送事件到队列
    public boolean fireEvent(EventModel model){
        try {
            Long event_qeue = redisTemplate.opsForList().leftPush("EVENT_QUEUE", model);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }


    }




}
