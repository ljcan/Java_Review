```
package rabbitmq.producer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 异步confirm
 */
public class ConfirmProducer {
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.confirmSelect();
        TreeSet<Long> confirmSet = new TreeSet<>();
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("消息发送成功，deliveryTag:"+deliveryTag+" multiple："+multiple);
                if(multiple){     //删除多条数据，即当前数据之前得所有数据已经被提交
                    confirmSet.headSet(deliveryTag-1).clear();
                }else {     //删除一条数据
                    confirmSet.remove(deliveryTag);
                }
            }
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("消息发送异常，deliveryTag:"+deliveryTag+" multiple："+multiple);
                if(multiple){     //删除多条数据，即当前数据之前得所有数据已经被提交
                    confirmSet.headSet(deliveryTag-1).clear();
                }else {     //删除一条数据
                    confirmSet.remove(deliveryTag);
                }
            }
        });
        while (true){      //模拟一直发送消息
            long nextNo = channel.getNextPublishSeqNo();
            channel.basicPublish("confirm-exchange","confirm",
                    MessageProperties.PERSISTENT_TEXT_PLAIN,("test message "+nextNo).getBytes());
            confirmSet.add(nextNo);
        }
    }
}

```
