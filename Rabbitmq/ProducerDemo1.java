```
package rabbitmq.producer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ProducerDemo1 {
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
            String message = "java client test!!!";
            channel.exchangeDeclare(EXCHANGE_NAME,"direct",true,false,null);
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"test1");
            channel.basicPublish(EXCHANGE_NAME,"test1",
                    MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());

            //设置消息的属性
//            channel.basicPublish("exchangeName","routingKey",
//                    new AMQP.BasicProperties().builder()
//                            .contentType("text/plain")
//            .deliveryMode(2)
//             .expiration("200")    //设置单条消息的过期时间
//            .priority(1)
//            .userId("")
//            .build(),"messageBytes".getBytes());
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
