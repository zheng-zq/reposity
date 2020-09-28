package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRibbon {
    @Autowired
    RestTemplate restTemplate;

    /*@Test
    public void testRibbon() {
        //确定要获取的服务名
        String serviceId = "XC-SERVICE-MANAGE-CMS";
        //ribbon客户端从eurekaService中根据服务名获取服务列表  eureka的作用就是可以自动获取域名和端口
//        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/page/get/5a754adf6abb500ad05688d9", Map.class);
        for (int i = 0; i < 1; i++) {
            ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://" + serviceId + "/cms/page/get/5a754adf6abb500ad05688d9", Map.class);
            Map body = forEntity.getBody();
            System.out.println(body);
        }
    }*/

}
