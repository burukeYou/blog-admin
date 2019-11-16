package com.myblog.async;

import com.myblog.enums.EventTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *  当一个类实现了这个接口（ApplicationContextAware）之后，
 *  这个类就可以方便获得ApplicationContext中的所有bean，可以调取spring容器中管理的各个bean。
 *
 *
 *
 *
 */

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {


    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);


    //spring上下文环境
    private ApplicationContext applicationContext;

    //事件处理器映射表
    private Map<EventTypeEnum, List<EventHandler>> config = new HashMap<>();

    @Autowired
    private RedisTemplate redisTemplate;



    /**
     * 初始化bean时执行的方法
     *      维护一个事件处理器映射表，根据事件模型的事件类型分发事件处理器去处理
     *
     *      Like --》【PraiseHandler】
     *      UNLIKE  -->[PraiseHandler]    一个处理器处理两个事件
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //1-遍历所有实现了EventHanler接口的子类
        Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);

        if (beans != null){
            //遍历所有事件处理器
            for (Map.Entry<String,EventHandler> e : beans.entrySet()){
                //获取该事件处理器支持的事件类型
                List<EventTypeEnum> supportEvenTypes = e.getValue().getSupportEvenTypes();

                for (EventTypeEnum evenType : supportEvenTypes) {
                    if (!config.containsKey(evenType)){
                        config.put(evenType,new ArrayList<EventHandler>());
                    }

                    config.get(evenType).add(e.getValue());

                }

            }
        }


        //2-开启线程去处理事件队列
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(()->{
            while(true){

/*
                System.out.println("处理事件");
*/
                //出队一个事件           --rightPop等价于jedis的brpop：移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
                Object res = null;
                try {
                    res = redisTemplate.opsForList().rightPop("EVENT_QUEUE",59, TimeUnit.SECONDS);

                } catch (Exception e) {
                    e.printStackTrace();
                }

             /*   System.out.println("000"+res);*/
                if (res == null)
                    continue;

                //调用该事件对应的所有事件处理器去处理
                EventModel model = (EventModel)res;
                if (!config.containsKey(model.getType())){
                    logger.error("不能识别的事件！");
                    continue;
                }

                //1-找到事件模型的事件类型对应的所有事件处理器
                for (EventHandler handler : config.get(model.getType())) {
                    System.out.println("处理时事件："+model);
                    handler.doHandle(model);
                }


            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext = applicationContext;
    }






}
