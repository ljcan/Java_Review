多队列、多线程、队列与线程隔离、任务异步处理、基于事件驱动的模型，其实就是AKKA的基本框架。


1、第一步就是通过ActorSystem创建一个Actor实例；

2、第二步就是调用ActorRef的tell方法，将消息发送给接受者；

3、第三步就是将tell出来的消息，先传递给ActorSystem共用的Dispatcher，然后会调用dispatch方法；

4、第四步就是执行dispatch方法中的mbox.enqueue(receiver.self, invocation)；

5、第五步就是执行dispatch方法中的registerForExecution(mbox, true, false)；

6、第六步就是线程在从mailbox的队列中取出一条消息后，最终会调用Actor的onReceive方法，去执行具体的业务处理逻辑。
