package producer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class ScheduleProducer {
    public static void main(String[] args) throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr(":9876");
        producer.start();
        for (int i = 0; i < 100; i++) {
            Message message = new Message("testTopic", ("Hello scheduled message " + i).getBytes());
            // This message will be delivered to consumer 3 seconds later.
            message.setDelayTimeLevel(3);
            producer.send(message);
        }
        producer.shutdown();
    }

}
