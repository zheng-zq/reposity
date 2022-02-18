package com.xuecheng.test.fastdfs;

import org.assertj.core.util.Maps;
import org.json.JSONObject;
import org.json.JSONString;
import org.junit.Test;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class Test00 {
    public static void main(String[] args) {
        // int [] arr=new int[4]{3,4,5,6};
        // float f=5+5.5;
        // String s= "join"+ "was"+ "here";
        int i = 10, j = 25, x = 30;
        switch (j - i) {
            case 15:
                x++;
            case 16:
                x += 2;
            case 17:
                x += 3;
            default:
                --x;
        }
        System.out.println(x);

    }

    @Test
    public void sss() {
        System.out.println("fsfdsfds");
        try {
            this.ttt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("sssssssssss");
    }

    @Test
    public void ttt() {
        // User user = new User();
        // user.setName("哈哈");
        // user.setPassword("123");
        // user.setMoney(100d);
        // map.put("abc", user);

        Map<Object, Object> map = new HashMap<>();
        map.put("1", "z");
        map.put("2", "z");
        map.put("3", "q");
        String s = map.toString();
        //转成json
        String jsonString = JSON.toJSONString(map);
        System.out.println("");
    }
}
