package producer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import javax.swing.plaf.IconUIResource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AsyncProducer {
    public static void main(String[] args) throws Exception{
        DefaultMQProducer producer=new DefaultMQProducer("test_group");
        producer.setNamesrvAddr(":9876");
        producer.start();
        final CountDownLatch latch = new CountDownLatch(100);
        for(int i=0;i<100;i++){
            try {
                final int index = i;
                Message message = new Message("testTopic", "tagB", "orderId1",
                        "Hello Word".getBytes(RemotingHelper.DEFAULT_CHARSET));
                producer.send(message, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        latch.countDown();
                        System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                    }

                    @Override
                    public void onException(Throwable e) {
                        latch.countDown();
                        System.out.printf("%-10d OK %s %n", index, e);
                        e.printStackTrace();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        latch.await(5, TimeUnit.SECONDS);
        producer.shutdown();

    }
}
