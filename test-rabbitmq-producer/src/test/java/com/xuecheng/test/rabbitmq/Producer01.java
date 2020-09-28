package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

//rabbitmq入门程序  //RabbitMq producter 生产者
public class Producer01 {

    //队列常量
    private static final String QUEUE = "helloword";

    public static void main(String[] args) {
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置主机
        connectionFactory.setHost("127.0.0.1");
        //设置端口    //15672是他的管理段端口
        connectionFactory.setPort(5672);
        //用户名
        connectionFactory.setUsername("guest");
        //密码
        connectionFactory.setPassword("guest");
        //设置虚拟主机,一个mq可以设置多个虚拟机,每个虚拟机就相当于一个独立的mq

        connectionFactory.setVirtualHost("/");//"/" 表示默认虚拟机

        Connection connection = null;
        Channel channel = null;
        try {
            //建立新连接
            connection = connectionFactory.newConnection();
            //创建回话通道,生产者和mq服务所有通信都在channel通道中完成
            channel = connection.createChannel();
            /**
             * 声明队列，如果Rabbit中没有此队列将自动创建
             * 参数明细:
             * queue:队列名称
             * durable:是否持久化,如果持久化,mq重启后队列还在
             * exclusive:队列是否独占此连接,队列只允许在该连接中访问,如果connection连接关闭队列则自动删除,如果将此参数设置为true可用于临时队列的创建
             * autoDelete:队列不再使用时是否自动删除此队列,如果将此参数和exclution设置为true就可以实现临时队列
             * argument:队列参数,比如设置存活时间
             */
            channel.queueDeclare(QUEUE, true, false, false, null);
            String message = "helloworld小明" + System.currentTimeMillis();
            /**
             * 消息发布方法
             * param1：Exchange交换机的名称，如果没有指定，则使用Default Exchange
             * param2:routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
             * param3:消息包含的属性
             * param4：消息体
             */
            /**
             * 这里没有指定交换机，消息将发送给默认交换机，每个队列也会绑定那个默认的交换机，但是不能显示绑定或解除绑定
             *　默认的交换机，routingKey等于队列名称
             */
            channel.basicPublish("", QUEUE, null, message.getBytes());
            System.out.println("send to mq" + message);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //生产者需要关闭频道和连接,消费者不用,因为消费者需要一直监听
            try {
                channel.close();
            } catch (Exception e) {
                try {
                    connection.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


}
