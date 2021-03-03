```
package rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 死信队列
 */
public class DeadLetterProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("exchange.normal","direct",true,false,null);
        channel.exchangeDeclare("exchange.dead","fanout",true);
        Map<String,Object> map = new HashMap<>();
        map.put("x-message-ttl",10000);       //消息10秒过期
        map.put("x-dead-letter-exchange","exchange.dead");    //设置死信交换器
        map.put("x-dead-letter-routing-key","routingkey");
        channel.queueDeclare("queue.normal",true,false,false,map);
        channel.queueDeclare("queue.dead",true,false,false,null);
        channel.queueBind("queue.normal","exchange.normal","normal");
        channel.queueBind("queue.dead","exchange.dead","routingkey");
        channel.basicPublish("exchange.normal","normal",
                MessageProperties.PERSISTENT_TEXT_PLAIN,"dead message".getBytes());

    }
}

```
