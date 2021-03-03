```
package rabbitmq.monitor;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionDemo {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            Connection connection = connectionFactory.newConnection();
            connection.addShutdownListener(new ShutdownListener() {
                @Override
                public void shutdownCompleted(ShutdownSignalException cause) {

                }
            });
            connection.addBlockedListener(new BlockedListener() {
                @Override
                public void handleBlocked(String reason) throws IOException {

                }

                @Override
                public void handleUnblocked() throws IOException {

                }
            });
            Channel channel = connection.createChannel();
            //该队列中堆积的数据大小
            long msgCount = channel.messageCount("queue");
            //已经消费的数据大小
            long consumerCount = channel.consumerCount("queue");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}

```
