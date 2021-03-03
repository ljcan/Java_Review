package producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * ./mqadmin updateTopic -n 127.0.0.1:9876 -b 127.0.0.1:10911 -t testStream
 */
public class SyncProducer {
    public static void main(String[] args) throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("default_groups");
        producer.setNamesrvAddr(":9876");
        producer.setSendMsgTimeout(60000);
        producer.start();
        for (int i=0;i<100;i++){
            Message message = new Message("testStream",
                    "tagA",("hello rocketMQ"+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = producer.send(message);
            System.out.println(result);
        }
        producer.shutdown();

    }
}
