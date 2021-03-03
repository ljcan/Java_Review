package producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class FilterProducer {
    public static void main(String[] args) throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("filter_group");
        producer.setNamesrvAddr(":9876");
        producer.start();
        Message message = new Message("filter_topic","tagA",
                ("filter msg "+111).getBytes(RemotingHelper.DEFAULT_CHARSET));
        //设置属性
        message.putUserProperty("a",String.valueOf(1));
        SendResult sendResult = producer.send(message);
        System.out.println(sendResult.toString());
        producer.shutdown();
    }
}
