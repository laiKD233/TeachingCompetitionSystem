package com.teaching.competition.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, cn.hutool.json.JSONUtil.toJsonStr(value), timeout, unit);
    }

    public void setStr(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object get(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return cn.hutool.json.JSONUtil.toBean(value, Object.class);
    }

    public String getStr(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return cn.hutool.json.JSONUtil.toBean(value, clazz);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
