package com.xuecheng.test.freemarker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuecheng.test.freemarker.model.Student;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * @Author ZZQ
 * @Date 2021/5/22 22:53
 * @Version 1.0
 */
public class Test {
    @org.junit.Test
    public void name() {
        // Student student = new Student();
        // student.setAge(1);
        // student.setName("郑州");
        // student.setMoney(1.0F);
        // String string = student.toString();
        // String jsonString = JSON.toJSONString(student);
        // System.out.println("---------------------");
        //
        // // JSONObject stringObject = JSON.parseObject(string);
        // Student jsonStringObject = JSON.parseObject(jsonString,Student.class);
        // System.out.println();

        List<String> list = Lists.newArrayList();
        list.add("郑");
        list.add("州");
        String stringList = JSON.toJSONString(list);
        List list1 = JSON.parseObject(stringList, List.class);
        System.out.println();


    }
}
