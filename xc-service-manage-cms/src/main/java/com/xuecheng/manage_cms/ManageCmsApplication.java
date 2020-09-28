package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient//一个EurekaClient从EurekaService发现服务
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")//扫描实体类
@ComponentScan(basePackages={"com.xuecheng.api"})//扫描接口
@ComponentScan(basePackages={"com.xuecheng.manage_cms"})//扫描本项目下的
@ComponentScan(basePackages={"com.xuecheng.framework"})//扫描common下的包
public class ManageCmsApplication {
    public static void main(String[] args) {
        System.out.println("ManageCmsApplication启动开始...");
        SpringApplication.run(ManageCmsApplication.class,args);
        System.out.println("ManageCmsApplication启动结束...");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

}
