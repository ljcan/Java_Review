package producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;

public class BatchProducer {
    public static void main(String[] args) throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("batch_group");
        producer.setNamesrvAddr(":9876");
        producer.start();
        String topic = "testTopic";
        List<Message> listMsg = new ArrayList<>();
        listMsg.add(new Message(topic,"tagB","order01",("list msg 01").getBytes()));
        listMsg.add(new Message(topic,"tagB","order02",("list msg 02").getBytes()));
        listMsg.add(new Message(topic,"tagB","order03",("list msg 03").getBytes()));
        producer.send(listMsg);
        producer.shutdown();
    }
}
