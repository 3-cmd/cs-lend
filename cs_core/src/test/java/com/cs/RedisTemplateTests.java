package com.cs;

import com.cs.core.CoreApplication;
import com.cs.core.mapper.DictMapper;
import com.cs.core.pojo.entity.Dict;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = CoreApplication.class)
@RunWith(SpringRunner.class)
public class RedisTemplateTests {
    @Resource
    private RedisTemplate redisTemplate;
    @Autowired
    private DictMapper dictMapper;

    @Test
    public void saveDict(){
        Dict dict = dictMapper.selectById(1);
        //向数据库中存储string类型的键值对, 过期时间5分钟
        redisTemplate.opsForValue().set("dict", dict, 5, TimeUnit.MINUTES);
        System.out.println(redisTemplate.opsForValue().get("dict"));
    }
}