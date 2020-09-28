package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//RabbitMq消费者
public class Consumer01 {
    //队列常量
    private static final String QUEUE = "helloword";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory connectionFactory = new ConnectionFactory(); //创建连接工厂
        connectionFactory.setHost("127.0.0.1");//设置MabbitMQ所在服务器的主机
        connectionFactory.setPort(5672);//设置端口  15672是他的管理段端口
        connectionFactory.setUsername("guest");//用户名
        connectionFactory.setPassword("guest");//密码

        connectionFactory.setVirtualHost("/"); //设置虚拟主机,一个mq可以设置多个虚拟机,每个虚拟机就相当于一个独立的mq  "/"表示默认虚拟机

        Connection connection = connectionFactory.newConnection();//建立新连接
        Channel channel = connection.createChannel();//创建会话通道,生产者和mq服务所有通信都在channel通道中完成
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

        //消费方法
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {


            /**
             * 当接收到消息后所要执行的方法
             * @param consumerTag 消费者标签
             * @param envelope 信封
             * @param properties 属性
             * @param body
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String s1 = new String(body, "utf-8");
                System.out.println(s1);
            }
        };
        //监听队列

        /*
         * queue队列名称
         * autoAck自动回复,当消费者接收到消息后要告诉mq消息已接收,如果设为true则自动回复,false则要通过编程自定义回复
         * callback消费方法,当消费者接收到消息后所要执行的方法
         */

        channel.basicConsume(QUEUE, true, defaultConsumer);


    }
}
