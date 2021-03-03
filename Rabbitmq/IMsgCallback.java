```
package rabbitmq.demo;

public interface IMsgCallback {
     ConsumeStatus consumeMsg(Message message);
}

```
