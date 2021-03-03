```
package rabbitmq.demo;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitEncapsulation {
    private static String host = "localhost";
    private static int port = 5672;
    private static String vhost = "/";
    private static String username = "";
    private static String pwd = "";
    private static Connection connection;
    private int  subdivisionNum;    //分片数，表示一个逻辑队列背后的实际队列数
    private ConcurrentLinkedDeque<Message> queue;

    public RabbitEncapsulation(int subdivisionNum,ConcurrentLinkedDeque<Message> queue){
        this.queue=queue;
        this.subdivisionNum=subdivisionNum;
    }

    public static void newConnection() throws IOException, TimeoutException {
        ConnectionFactory  factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(pwd);
        factory.setHost(host);
        factory.setVirtualHost(vhost);
        factory.setPort(port);
        connection = factory.newConnection();
    }

    public static Connection getConnection() throws IOException, TimeoutException {
        if(connection==null){
            newConnection();
        }
        return connection;
    }

    public static void closeConnection() throws IOException {
        if(connection!=null){
            connection.close();
        }
    }

    //声明交换器
    public void exchanegDeclare(Channel channel,String exchange,String type,boolean durable,boolean autoDelete,
                                Map<String,Object> args) throws IOException {
        channel.exchangeDeclare(exchange,type,durable,autoDelete,autoDelete,args);
    }

    //声明队列
    public void queueDeclare(Channel channel,String queue,boolean durable,boolean exclusive,boolean autoDelete,
                             Map<String,Object> args) throws IOException {
        for(int i=0;i<subdivisionNum;i++){
            String queueName = queue+"_"+i;
            channel.queueDeclare(queueName,durable,exclusive,autoDelete,args);
        }
    }

    //创建绑定关系
    public void queueBind(Channel channel,String queue,String exchange,String routingKey,Map<String,Object> args) throws IOException {
        for (int i = 0; i < subdivisionNum; i++) {
            String rkName = queue+"_"+i;
            String queueName = queue+"_"+i;
            channel.queueBind(queueName,exchange,rkName,args);
        }
    }

    //消费者拉模式封装
    public GetResponse basicGet(Channel channel,String queue,boolean autoAck) throws IOException {
        GetResponse response = null;
        Random random = new Random();
        int index = random.nextInt(subdivisionNum);
        response = channel.basicGet(queue+"_"+index,autoAck);
        if(response==null){
            for(int i=0;i<subdivisionNum;i++){
                String queueName = queue+"_"+i;
                response = channel.basicGet(queueName,autoAck);
                if(response!=null){
                    return response;
                }
            }
        }
        return response;
    }

    //消费者推模式封装
    public void startConsume(Channel channel,String queue,boolean autoAck,String consumerTag,
                             ConcurrentLinkedDeque<Message> newblockqueue) throws IOException {
        for (int i = 0; i < subdivisionNum; i++) {
            String queueName = queue+"_"+i;
            channel.basicConsume(queueName,autoAck,consumerTag+i,new NewConsumer(channel,newblockqueue));
        }
    }
    public void basicConsume(Channel channel,String queue,boolean autoAck,String consumerTag,
                             ConcurrentLinkedDeque<Message> newblockingqueue,IMsgCallback iMsgCallback) throws IOException {
            startConsume(channel,queue,autoAck,consumerTag,newblockingqueue);
            while (true){
                //消费者消费数据的缓存
                Message message = newblockingqueue.peekFirst();
                if(message!=null){
                    ConsumeStatus consumeStatus = iMsgCallback.consumeMsg(message);
                    newblockingqueue.removeFirst();
                    if(consumeStatus==ConsumeStatus.SUCCESS){
                        channel.basicAck(message.getDeliveryTag(),false);
                    }else{
                        channel.basicReject(message.getDeliveryTag(),false);
                    }
                }else{
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
    }

    public static class NewConsumer extends DefaultConsumer{

        private ConcurrentLinkedDeque<Message> newblockqueue;

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public NewConsumer(Channel channel,ConcurrentLinkedDeque<Message> newblockqueue) {
            super(channel);
            this.newblockqueue = newblockqueue;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            try{
                Message message = (Message) Message.getObjectFromBytes(body);
                message.setDeliveryTag(envelope.getDeliveryTag());
                newblockqueue.addLast(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Channel channel=connection.createChannel();
        channel.basicQos(64);
        RabbitEncapsulation rabbitEncapsulation = new RabbitEncapsulation(4,new ConcurrentLinkedDeque<>());
        rabbitEncapsulation.basicConsume(channel, "queue", false, "consumer_ra",
                rabbitEncapsulation.queue, new IMsgCallback() {
                    @Override
                    public ConsumeStatus consumeMsg(Message message) {
                        ConsumeStatus status = ConsumeStatus.FAIL;
                        if(message!=null){
                            System.out.println(message);
                            status = ConsumeStatus.SUCCESS;
                        }
                        return status;
                    }
                });
    }
}

```
