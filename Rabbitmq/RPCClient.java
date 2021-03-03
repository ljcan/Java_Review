```
package rabbitmq.producer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;

public class RPCClient {
    static boolean isRec = false;
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("");
        factory.setUsername("");
        factory.setPassword("");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("rpc-exchange","direct",true,false,null);
        channel.queueDeclare("rpc-queue",true,false,false,null);
        channel.queueBind("rpc-queue","rpc-exchange","rpc");
        String replyQueueName = channel.queueDeclare().getQueue();
        Consumer consumer = new DefaultConsumer(channel);
        channel.basicConsume(replyQueueName,true,consumer);
        String corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties properties = new AMQP.BasicProperties()
                .builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();
        channel.basicPublish("rpc-exchange","rpc",properties,"test client".getBytes());

        while(true){
            channel.basicConsume("rpc-queue",new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String RecCorrId = properties.getCorrelationId();
                    if(corrId.equals(RecCorrId)){
                        String response = new String(body);
                        System.out.println("客户端发送信息 :"+response);
                        String replyTo = properties.getReplyTo();
                        System.out.println("reply: "+replyTo);
                        isRec = true;
                    }

                }

            });
            if(isRec){
                break;
            }
        }


    }
}

```
