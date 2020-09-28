package com.xuecheng.manage_cms_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类
 */
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")//扫描model模块里的实体类
@ComponentScan(basePackages = "com.xuecheng.framework")//扫描common下的所有类
@ComponentScan(basePackages = "com.xuecheng.manage_cms_client")//扫描cms模块的下包
public class ManageCmsClientApplication {
    public static void main(String[] args) {
        System.out.println("ManageCmsClientApplication启动开始...");
        SpringApplication.run(ManageCmsClientApplication.class,args);
        System.out.println("ManageCmsClientApplication启动结束...");
    }
}
