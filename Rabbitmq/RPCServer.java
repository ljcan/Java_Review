```
package rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * MQ实现RPC调用，服务端代码
 */
public class RPCServer {
    static ConnectionFactory factory = null;
    static Connection connection = null;
    static Channel channel = null;
    static Consumer consumer = null;
    public static void main(String[] args) throws IOException {
       try{
           factory = new ConnectionFactory();
           factory.setHost("0.0.0.0");
           factory.setUsername("");
           factory.setPassword("");
           connection = factory.newConnection();
           channel = connection.createChannel();
           channel.exchangeDeclare("rpc-exchange","direct",true,false,null);
           channel.queueDeclare("rpc-queue",true,false,false,null);
           channel.basicQos(1);
           channel.queueBind("rpc-queue","rpc-exchange","rpc");
           consumer = new DefaultConsumer(channel){
               @Override
               public void handleDelivery(String consumerTag,
                                          Envelope envelope,
                                          AMQP.BasicProperties properties,
                                          byte[] body) throws IOException {
                   AMQP.BasicProperties replyProps = new AMQP.BasicProperties()
                           .builder()
                           .correlationId(properties.getCorrelationId())
                           .build();
                   String response = "服务端收到";
                   String message = new String(body,"UTF-8");
                   System.out.println(message);
                   channel.basicPublish("rpc-exchange",properties.getReplyTo(),
                           replyProps,response.getBytes("UTF-8"));
                   channel.basicAck(envelope.getDeliveryTag(),false);
               }
           };
       }catch (Exception e){
           e.printStackTrace();
       }finally {

       }
       channel.basicConsume("rpc-queue",false,consumer);
    }
}

```
