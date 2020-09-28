package com.xuecheng.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 * @version 1.0
 **/
//创建测试需要的客户端
@Configuration
public class ElasticsearchConfig {

    @Value("${xuecheng.elasticsearch.hostlist}")
    private String hostlist;

    //高级的客户端  项目主要使用RestHighLevelClient
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        //1 解析hostlist配置信息
        String[] split = hostlist.split(",");
        //2 创建HttpHost数组，其中存放es主机和端口的配置信息
        HttpHost[] httpHostArray = new HttpHost[split.length];
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            httpHostArray[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "http");
        }
        //3 创建RestHighLevelClient高级客户端
        return new RestHighLevelClient(RestClient.builder(httpHostArray));
    }

    //低级的客户端
    @Bean
    public RestClient restClient() {
        //1 解析hostlist配置信息
        String[] split = hostlist.split(",");
        //2 创建HttpHost数组，其中存放es主机和端口的配置信息
        HttpHost[] httpHostArray = new HttpHost[split.length];
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            httpHostArray[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "http");
        }
        //3 返回RestClient低级客户端
        return RestClient.builder(httpHostArray).build();
    }

}
