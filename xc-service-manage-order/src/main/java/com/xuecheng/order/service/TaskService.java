package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired(required = false)
    XcTaskRepository xcTaskRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    //取出前n条任务,取出指定时间之前处理的任务
    public List<XcTask> findTaskList(Date updateTime, int n) {
        //设置分页参数,取出前n条记录0, n
        // PageRequest pageable = new PageRequest(0, n);
        //获取指定时间前的总任务数并分页
        Page<XcTask> xcTasks = xcTaskRepository.findByUpdateTimeBefore(PageRequest.of(0, n), updateTime);
        //取出分页后任务的内容
        List<XcTask> content = xcTasks.getContent();
        return content;
    }

    //发送消息
    @Transactional(rollbackOn=Exception.class)
    public void publish(XcTask xcTask, String ex, String routingKey) {//xcTask 任务对象  ex 交换机id

        //查询任务
        Optional<XcTask> taskOptional = xcTaskRepository.findById(xcTask.getId());
        if (taskOptional.isPresent()) {
            XcTask one = taskOptional.get();
            //使用rabbitMq发送消息需引用rabbitTemplate
            rabbitTemplate.convertAndSend(ex, routingKey, one);
            //更新任务时间为当前时间
            one.setUpdateTime(new Date());
            //更新操作
            xcTaskRepository.save(one);
        }
    }


    //乐观锁方法更新数据表,如果结果大于0说明取到任务
    @Transactional(rollbackOn=Exception.class)
    public int getTask(String taskId, int version) {
        int i = xcTaskRepository.updateTaskVersion(taskId, version);
        return i;
    }

    //根据任务id删除当前已完成任务
    @Transactional(rollbackOn=Exception.class)
    public void finishTask(String taskId) {
        //根据任务id查询当前任务
        Optional<XcTask> taskOptional = xcTaskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            //当前任务
            XcTask xcTask = taskOptional.get();
            //历史任务
            xcTask.setDeleteTime(new Date());
            XcTaskHis xcTaskHis = new XcTaskHis();
            //将当前任务属性复制到历史任务中
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            //保存历史任务
            xcTaskHisRepository.save(xcTaskHis);
            //删除当前任务
            xcTaskRepository.delete(xcTask);
        }
    }

}
