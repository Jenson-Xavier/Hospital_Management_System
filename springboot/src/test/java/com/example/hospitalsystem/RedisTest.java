package com.example.hospitalsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

@SpringBootTest     // 单元测试方法执行之前，会先初始化Spring容器
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testSet() {
        // 向redis中存储一个键值对
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set("username", "张三");
        operations.set("id", "1" , 15, TimeUnit.SECONDS);   // 过期时间15s
    }

    @Test
    public void testGet() {
        // 从redis中获取一个键值对
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        System.out.println(operations.get("username"));
    }

}
