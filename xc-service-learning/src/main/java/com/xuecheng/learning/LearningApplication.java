package com.xuecheng.learning;

import com.mysql.jdbc.log.LogUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-14 11:11
 **/
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EntityScan(value = {"com.xuecheng.framework.domain.learning", "com.xuecheng.framework.domain.task"})//扫描实体类
@ComponentScan(basePackages = {"com.xuecheng.api"})//扫描接口
@ComponentScan(basePackages = {"com.xuecheng.learning"})//扫描接口
@ComponentScan(basePackages = {"com.xuecheng.framework"})//扫描common下的所有类
public class LearningApplication {

    public static void main(String[] args) throws Exception {
        System.out.println("LearningApplication启动开始...");
        SpringApplication.run(LearningApplication.class, args);
        System.out.println("LearningApplication启动结束...");
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

}