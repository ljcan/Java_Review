```
package rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.security.auth.callback.CallbackHandler;
import java.util.Deque;

public class TranactiohnProducer {
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //事务模式
//        try{
//            channel.txSelect();
//            channel.basicPublish();
//            int result =1/0;
//            channel.txCommit()
//        }catch (Exception e){
//            e.printStackTrace();
//            channel.txRollback();
//        }

        //发送方确认模式
//        channel.confirmSelect();
//        channel.basicPublish();
//        if(!channel.waitForConfirms()){
//            System.out.println("send message failed!");
//        }


    }
}

```
