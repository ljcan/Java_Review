```
package spark;

import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spark.*;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class Rocketmq2SparkStreaming {
    final static String NAMESERVER_ADDR = "";
    final static String CONSUMER_GROUP = "";
    final static String CONSUMER_TOPIC = "";
    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf()
                .setAppName("Rocketmq2SparkStreaming")
                .setMaster("local");
        StreamingContext context = new StreamingContext(conf, Durations.seconds(5));
        JavaStreamingContext jssc = new JavaStreamingContext(context);

//        Map<TopicQueueId,  OffsetRange[]> offsetRanges = new HashMap<>();
//        TopicQueueId topicQueueId1 = new TopicQueueId("topic", 1);
//        OffsetRange [] ranges1 = {OffsetRange.create("groupId", 1, "broker-1", 0, 100),
//                OffsetRange.create("groupId", 1, "broker-2", 0, 100)};
//        offsetRanges.put(topicQueueId1, ranges1);
//
//        TopicQueueId topicQueueId2 = new TopicQueueId("topic", 2);
//        OffsetRange [] ranges2 = {OffsetRange.create("groupId", 2, "broker-1", 0, 100),
//                OffsetRange.create("groupId", 2, "broker-2", 0, 100)};
//        offsetRanges.put(topicQueueId2, ranges2);

//        JavaInputDStream<MessageExt> javaMQPullStream = RocketMqUtils.createJavaMQPullStream(javaStreamingContext, "sparkGroup",
//                "", ConsumerStrategy.earliest(),
//                true, false, false);

        Properties properties = new Properties();
        properties.setProperty(RocketMQConfig.NAME_SERVER_ADDR, NAMESERVER_ADDR);
        properties.setProperty(RocketMQConfig.CONSUMER_GROUP, CONSUMER_GROUP);
        properties.setProperty(RocketMQConfig.CONSUMER_TOPIC, CONSUMER_TOPIC);

        JavaInputDStream<Message> javaMQPushStream = RocketMqUtils.createJavaMQPushStream(jssc, properties, StorageLevel.MEMORY_ONLY());

        javaMQPushStream.foreachRDD(new VoidFunction<JavaRDD<Message>>() {
            @Override
            public void call(JavaRDD<Message> messageJavaRDD) throws Exception {
                messageJavaRDD.foreachPartition(new VoidFunction<Iterator<Message>>() {
                    @Override
                    public void call(Iterator<Message> messageIterator) throws Exception {
                        while (messageIterator.hasNext()){
                            System.out.println("TransactionId="+messageIterator.next().getTransactionId()+"  body:"+
                                    messageIterator.next().getBody());
                        }
                    }
                });
            }
        });
        //启动任务
        jssc.start();
        jssc.awaitTermination();
    }
}

```
