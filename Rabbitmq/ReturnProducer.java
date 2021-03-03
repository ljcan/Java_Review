```
package rabbitmq.producer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReturnProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("");
        factory.setUsername("");
        factory.setPassword("");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("return-exchange","direct",true,false,null);
        channel.queueDeclare("return-queue",true,false,false,null);
        channel.queueBind("return-queue","return-exchange","ret");
        channel.basicPublish("return-exchange","res1",true, MessageProperties.PERSISTENT_TEXT_PLAIN,
                "return message".getBytes());
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange,
                                     String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body);
                System.out.println("返回的消息是： "+message);
            }
        });
        System.out.println("发送消息");
    }
}

```
