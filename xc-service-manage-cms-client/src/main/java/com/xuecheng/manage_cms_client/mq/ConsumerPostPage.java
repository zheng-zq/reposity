package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听MQ,接收页面发布消息
 */
@Component
public class ConsumerPostPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    PageService pageService;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})//从配置文件中获取队列名称
    public void postPage(String msg) {
        //接收到页面后
        Map map = JSON.parseObject(msg, Map.class);//解析页面
        String pageId = (String) map.get("pageId");//得到页面id
        CmsPage cmsPage = pageService.findCmsPageById(pageId);
        if (cmsPage == null) {
            LOGGER.error("recive postpage msg,cmsPage is null,pageId:{}", pageId);
            return;
        }
        pageService.savePageToServerPath(pageId);//将页面从GridFs中下载到服务器
    }
}
