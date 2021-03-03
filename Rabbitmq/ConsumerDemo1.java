```
package rabbitmq.consumer;

import com.google.common.primitives.Bytes;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConsumerDemo1 {
    static final String EXCHANGE_NAME = "test-exchange";
    static final String QUEUE_NAME = "test-queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = null;
        Connection connection = null;
        Channel channel = null;
        try{
            factory = new ConnectionFactory();
            factory.setHost("");
            factory.setUsername("");
            factory.setPassword("");
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME,"direct",true,false,null);
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"test1");
            //一次消费多少条数据
            channel.basicQos(64);
            channel.basicConsume(QUEUE_NAME,new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("msg: "+ new String(body));
                    super.handleDelivery(consumerTag, envelope, properties, body);
                }

                @Override
                public void handleConsumeOk(String consumerTag) {
                    System.out.println("msgTag: "+consumerTag);
                    super.handleConsumeOk(consumerTag);
                }

                @Override
                public Channel getChannel() {
                    System.out.println("获取信道");
                    return super.getChannel();
                }
            });
            TimeUnit.SECONDS.sleep(10);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(channel!=null){
                channel.close();
            }
            if(connection!=null){
                connection.close();
            }
        }

    }
}

```
