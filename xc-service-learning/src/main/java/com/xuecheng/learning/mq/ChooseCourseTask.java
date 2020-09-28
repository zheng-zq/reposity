package com.xuecheng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.service.LearningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.Channel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    LearningService learningService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 接收选课任务
     */
    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE})
    public void receiveChoosecourseTask(XcTask xcTask, Message message, Channel channel) throws IOException {
        LOGGER.info("receive choose course task,taskId:{}", xcTask.getId());
        //接收到的消息id
        String id = xcTask.getId();
        //添加选课
        try {
            String requestBody = xcTask.getRequestBody();
            Map map = JSON.parseObject(requestBody, Map.class);
            String userId = (String) map.get("userId");
            String courseId = (String) map.get("courseId");
            String valid = (String) map.get("valid");
            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            if (map.get("startTime") != null) {
                startTime = dateFormat.parse((String) map.get("startTime"));
            }
            if (map.get("endTime") != null) {
                endTime = dateFormat.parse((String) map.get("endTime"));
            }
            //1 添加选课
            ResponseResult addcourse = learningService.addcourse(userId, courseId, valid, startTime, endTime, xcTask);
            if (addcourse.isSuccess()) {
                //2.1 选课成功向MQ发送响应消息
                rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE, RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY, xcTask);
                LOGGER.info("send finish choose course taskId:{}", id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //2.2 选课不成功打印错误信息
            LOGGER.error("send finish choose course taskId:{}", id);
        }
    }
}