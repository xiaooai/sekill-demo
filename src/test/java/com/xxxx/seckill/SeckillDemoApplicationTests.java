package com.xxxx.seckill; 

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import com.xxxx.seckill.SeckillDemoApplication;

@SpringBootTest(classes = SeckillDemoApplication.class)
class SeckillDemoApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Boolean> script;

    @Test
    public void testLock01() {
        System.out.println("测试");
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 占位，如果key不存在才可以设置
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1");
        if (isLock) {
            System.out.println("设置成功");
            valueOperations.set("name", "xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name:" + name);
            // 操作结束，删除锁
            redisTemplate.delete("k1"); 

        } else {
            System.out.println("设置失败");
        }

    }

    @Test
    public void testLock02() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        if (isLock) {
            System.out.println("设置成功");
            valueOperations.set("name", "xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name:" + name);
            // 操作结束，删除锁
            Integer.parseInt("xxxx");
            redisTemplate.delete("k1"); 
        } else {
            System.out.println("设置失败");
        }
    }

    @Test
    public void testLock03() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        Boolean isLock = valueOperations.setIfAbsent("k1", value, 120 , TimeUnit.SECONDS);
        if (isLock) {   
            valueOperations.set("name", "xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name:" + name);
            System.out.println(valueOperations.get("k1"));
            Boolean result = (Boolean) redisTemplate.execute(script, Collections.singletonList("k1"), value);
            System.out.println("result:" + result);
        } else {
            System.out.println("设置失败");
        }
    }
}
