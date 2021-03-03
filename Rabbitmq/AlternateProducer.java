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
 * 备份交换器防止数据丢失
 */
public class AlternateProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        Map<String,Object> argsMap = new HashMap<>();
        argsMap.put("alternate-exchange","myAe");
        channel.exchangeDeclare("normal-exchange","direct",true,false,argsMap);
        channel.exchangeDeclare("myAe","fanout",true,false,null);
        Map<String,Object> queueMap = new HashMap<>();
        queueMap.put("x-message-ttl",100);    //设置队列消息的过期时间，单位为毫秒
        channel.queueDeclare("normal-queue",true,true,false,queueMap);
        channel.queueBind("normal-queue","normal-exchange","normal-key");
        channel.queueDeclare("ae-queue",true,true,false,null);
        channel.queueBind("ae-queue","myAe","");
        channel.basicPublish("normal-exchange","ae-key",
                MessageProperties.PERSISTENT_TEXT_PLAIN,"test ae message".getBytes());
        System.out.println("发送消息测试备份交换器，若是queue没有与exchange匹配绑定的key，那么将会把消息发送到备份交换器");
    }
}

```
