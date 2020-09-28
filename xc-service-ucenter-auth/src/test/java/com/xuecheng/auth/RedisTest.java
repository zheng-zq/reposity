package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        //定义key
        String key = "user_token:9734b68f-cf5e-456f-9bd6-df578c711390";
        //定义Map
        Map<String, String> mapValue = new HashMap<>();
        mapValue.put("id", "101");
        mapValue.put("username", "itcast");
        String value = JSON.toJSONString(mapValue);
        //向redis中存储字符串
        stringRedisTemplate.boundValueOps(key).set(value, 60, TimeUnit.SECONDS);
        //读取过期时间，已过期返回-2
        Long expire = stringRedisTemplate.getExpire(key);
        System.out.println(expire);
        //根据key获取value
        String s = stringRedisTemplate.opsForValue().get(key);
        System.out.println(s);
    }

    //BCryipt加密算法  每次加一个随即算法的*盐*
    //md5是盐固定
    @Test
    public void testPasswordEncoder() {
        //原始密码
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        for (int i = 0; i < 5; i++) {
            String encode = bCryptPasswordEncoder.encode("1");
            System.out.println(encode);
            //校验加密码和未加密是否一致
            System.out.println(bCryptPasswordEncoder.matches("1", encode));
        }
    }
}