package com.myblog.config;


import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

/**
 *      bean注解默认属性id为方法名
 *
 *
 *
 *              定时任务，定时刷新redis缓存。
 *
 */

@Configuration
@EnableCaching    //开启缓存
public class MyRedisConfig {


    /**
     *      自定义key生成策略
     */
    @Bean("myKeyGenerator")
    public KeyGenerator keyGenerator(){
        return new KeyGenerator(){

            @Override
            //参数：目标对象，方法，参数
            public Object generate(Object target, Method method, Object... params) {
                return method.getName()+"["+ Arrays.asList(params).toString()+"]";
            }
        };
    }




    /**         自定义RedisTemplate的序列化形式
     *
     *      修改IOC内对象RedisTemplate的默认序列化器（改为json）
     */
    @Bean("redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory){

        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        //template.setDefaultSerializer(new Jackson2JsonRedisSerializer<Employee>(Employee.class));
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());

        return template;

    }


    /**
     *      设置spring cache缓存的序列化方式
     *              使用StringRedisSerializer对Key进行序列化，
     *              使用GenericJackson2JsonRedisSerializer对Value进行反序列化。
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofHours(4))   //设置过期时间
                .serializeKeysWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }







}
