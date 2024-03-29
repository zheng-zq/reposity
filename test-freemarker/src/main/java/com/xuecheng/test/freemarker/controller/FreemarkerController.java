package com.xuecheng.test.freemarker.controller;


import com.xuecheng.test.freemarker.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RequestMapping("/freemarker")
@Controller//这里不要使用RestController,因为它返回的json字符串  我们要输出html网页所以用Controller
public class FreemarkerController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/course")
    public String course(Map<String, Object> map){
        //使用restTemplate请求轮播图的模型数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31200/course/courseview/4028e581617f945f01617f9dabc40000", Map.class);
        Map body = forEntity.getBody();
        //设置模型数据
        map.putAll(body);
        return "course";
    }



    @RequestMapping("/banner")
    public String index_banner(Map<String, Object> map){
        //使用restTemplate请求轮播图的模型数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = forEntity.getBody();
        //设置模型数据
        map.putAll(body);
        return "index_banner";
    }
    //测试1
    @RequestMapping("/test1")
    public String test1(Map<String, Object> map) {
        //map就是freemarker模版所使用的数据
        //向数据模型放数据
        map.put("name", "黑马程序员");

        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());
        //朋友列表
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        //给第二名学生设置学生列表
        stu2.setFriends(friends);
        //给第二名学生设置最好的朋友
        stu2.setBestFriend(stu1);
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //将学生列表放在map数据模型中
        map.put("stus", stus);
        //准备map数据
        HashMap<String, Student> stuMap = new HashMap<>(16);
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        //向数据模型放数据
        map.put("stu1", stu1);
        //向数据模型放map数据
        map.put("stuMap", stuMap);
        //返回模板文件名称
        return "test1";
    }
}
