**RocketMQ广泛应用于交易，数据同步，缓存同步，IM通讯，流计算、IoT等场景。保证了链路的消息流转的低延迟 ，高吞吐。**

启动nameserver：

`nohup sh mqnamesrv &`

启动broker：

`nohup sh bin/mqbroker -n localhost:9876&`

`broker.conf`文件的配置详解：

```
brokerClusterName = DefaultCluster  集群名称
brokerName = broker-a      broker名称
brokerId = 0      0表是master，大于0表示不同slave的id
deleteWhen = 04        与fileReservedTime参数呼应，表名在几点做消息删除动作，默认值04表示凌晨4点
fileReservedTime = 48     磁盘上保存消息的时长，单位是小时，自动删除超时的消息
brokerRole = ASYNC_MASTER        有三种值，ASYNC_MASTER、SYNC_MASTER、SLAVE
flushDiskType = ASYNC_FLUSH    刷盘策略，ASYNC-FLUSH异步刷盘，消息写入pagecache后就返回成功状态，             SYNC_FLUSH同步刷盘，消息真正写入磁盘后再返回成功状态。
enablePropertyFilter = true     是否支持消费者通过过滤条件来消费消息
```



#### 消费者

rocketmq支持两种消息模式：clustering和broadcasting

在clustering模式下，同一个ConsumerGroup里的每个consumer只消费所订阅消息的一部分内容，同一个consumerGroup里所有的Consumer消费的内容合起来才是所订阅Topic内容的整体，从而达到负载均衡的目的。这种模式下，offset存储在broker中，使用RemoteBrokerOffsetStore结构来存储。

在broadcasting模式下，同一个ConsumerGroup里的每个Consumer都消费到所订阅topic的全部消息，也就是一个消息会被多次分发，被多个consumer消费。offset存储在本地，使用LocalFileOffsetStore结构来存储。

如果使用PullConsumer，就需要自己处理offsetStore。

DefaultMQPushConsumer：由系统控制读取消息，收到消息后自动调用传入的处理方法来处理，系统收到消息后自动调用处理函数来处理消息，自动保存offset，而且新加入的consumer会自动做负载均衡。server端接手到消息后，主动把消息推送给client端，实时性高。

不过push情况下由于是有server端来控制消息的发送，主动推送，进而影响server的性能，其次client处理能力各不相同，如果client处理不及时，容易造成消息堆积。

DefaultMQPullConsumer：使用长轮询的方式，broker端hold住客户端过来的请求一小段时间，在这个时间段内有消息到达，就利用现有的连接立刻返回消息给consumer，“长轮询”的主动权还是在consumer手中，broker即使有大量的消息积压，也不会主动推送给consumer。（hold住consumer请求的时候需要占用资源，它适合用在消息队列这种客户端连接数可控的场景中）。

DefaultMQPushConsumer中有个线程池，消息处理逻辑在各个线程中同时执行，在PushConsumer运行的时候，每个MessageQueue都会有一个对应的ProcessQueue，保存这个消息处理状态的快照。PushConsumer会通过ProcessQueue来获取未处理消息的个数，消息的总大小，offset的跨度，任何一个值超过设定大小就隔一段时间再拉取消息，从而达到流量控制的目的。还可以借助ProcessQueue来实现顺序消费的逻辑。



一个topic下包括多个MessageQueue，如果一个consumer需要获取这个topic下的消息，就需要遍历所有的messagequeue，如果有必要，可以指定MessageQueue来读取数据。

从一个MessageQueue中拉取数据的时候，需要传入offset参数，随着不断读取消息，offset会不断增长，这个时候用户可以负责把offset存储下来，存储到内存，磁盘或者数据库中。

拉取消息的请求返回后，会返回：FOUND，NO_MATCHED_MSG，NO_NEW_MSG，OFFSET_ILLEGAL四种状态，FOUND和NO_NEW_MSG分别表示获取到消息和没有获取到新消息，可以根据这些状态来做不同的逻辑判断。



#### 生产者

RocketMQ有各种不同的生产者，不同的业务场景需要选择合适的生产者，比如同步发送，异步发送，延迟发送，发送事务消息等。

RocketMQ通过两阶段提交的方式来实现事务消息。



#### NameServer

NameServer是整个消息队列的状态服务器，集群的各个组件定期向NameServer上报自己的状态，超时不上报的话，NameServer会认为某个机器出故障不可用了，其他的组件会把这个机器从可用列表中剔除。

NameServer也可以部署一个或者多个，其他角色同时向多个NameServer来上报信息达到热备份的目的。nameserver本身是无状态的，只是将这些角色的信息放在内存中存储，不会持久化到磁盘。

RcoketMQ消息的存储是由ConsumerQueue和CommitLog配合完成的，消息真正的物理存储文件是CommitLog，ConsumerQueue是消息的逻辑队列，类似数据库的索引文件，存储的是指向物理存储的地址。

![rocketmq存储结构图](https://github.com/ljcan/jqBlogs/blob/master/rocketmq%E5%AD%98%E5%82%A8%E7%BB%93%E6%9E%84%E5%9B%BE.png)

commitLog存储在机器的${user.home}\store\\${commitLg}\\${fileName}位置，该文件被本机器上的所有ConsumerQueue共享，该机构顺序写入，可以大大的提高写入的效率；但是随机读取，利用操作系统的pagecache机制，可以批量地从磁盘读取，然后作为cache存储到内存中，后续加快读取的速度。

为了能够顺序写入，在ConsumerQueue里只存偏移信息，所以尺寸是有限的，在实际情况中，大部分的ConsumeQueue能够被全部读入内存，因此操作速度很快；为了保证CommitLog和ConsumerQueue保持一致，

CommitLog里存储了Consume Queues，Messages Key，Tag等所有信息，即使Consume Queue丢失，也可以通过commitLog完全恢复出来。



Borker一般分为两种角色，master和slave角色，master负责读写，slave只负责读，那么如何保证producer端的高可用呢？目前的版本，当slave宕机了，无法自动从slave切换到master，需要手动去切换。这时需要在创建topic的时候，把topic的多个message queue创建在多个broker组上（相同的broker名称，不同的brokerid的机器组成一个broker组），这样当一个broker组的master不可用后，其他组的master仍然可用，producer可以继续发送消息。

#### MQ刷盘策略

rocketmq的消息是存储到磁盘上的，这样既能保证断电户恢复，又可以保证消息量超出内存大小限制的限制，为了保证写的性能，会尽量采用顺序写的这种方式。

1. 同步刷盘：在返回消息写入成功的时候，这时消息已经写入pagecache，立即通知刷盘线程刷盘，刷盘线程写入成功后换醒等待线程，返回写入成功的状态。
2. 异步刷盘：消息只写入pagecahe就返回写入成功的状态，写操作快，吞吐量大，但是如果broker宕机，消息会丢失。当消息积累到一定的程度时，统一触发写磁盘动作，快速写入。

一个broker组有master和slave这两种角色，，需要让master将消息复制到slave，有同步复制和异步复制两种：

- 同步复制：数据写入master和slave都成功的时候才返回成功的消息。当master出故障的时候，消息已经全部同步到了slave，数据可以恢复，但是增加了数据写入的延迟，降低系统吞吐量。
- 异步复制：只要master写入成功就返回成功。系统有较低的延迟和高吞吐量，但是master出故障的时候，slave上没有写入数据，容易丢失。

通常情况下，应该把 Master和 Save配置成 ASYNC FLUSH 的刷盘方式，主从之间配置成 SYNC MASTER 的 复制方式，这 样即使有 一台机器出故障， 仍然能保证数据不丢，是个不错的选择 。



**消息重复消费**

消息重复一般情况下不会发生，但是如果消息量大，网络有波动，消息重复就是一个大概率事件，比如Producer有个函数setRetryTimesWhenSendFailed，设置在同步方式下自动重试的次数，默认值是2，这样当第一次发送消息时，broker端接收到消息后但是没有正确返回发送成功的状态，就造成了消息重复。

解决重复消费的方法有两种：

1. 保证消费逻辑的幂等性。
2. 维护一个已消费消息的记录，消费之前查看该消息是否被消费过。

**动态增减broker**

当系统只有一个broker master的时候，停掉该master，数据肯定会丢失，如果有多个master，数据是否会丢失，取决于producer，与发送消息的状态有关，如果使用同步的方式发送，那么在DefaultMQProducer内部有自动重试的逻辑，当发送到这台需要去掉的broker的时候，失败返回会重新发送到下一台broker，不会发生丢失的现象；如果是异步或者SendOnyWay的方式，会发生丢失切换的现象，Producer.RetryTimesWhenSendFailed设置不起作用，发生失败不会重试。DefaultMQProducer默认每30秒到nameserver中请求最新的路由消息，producer如果获取不到已经停止的broker的队列消息，后续就不会再向这些broker发送消息。

为了消除单点依赖，避免某台机器出现极端故障的时候也不会丢失消息：

1. 多 Master，每个 Master 带有 Slave;
2. 主从之间设置成 SYNC_MASTER;
3. Producer 用同步方式写;
4. 刷盘策略设置成 SYNC FLUSH。

**Broker端消息的过滤**

1. 通过tag进行过滤：一个Message只能有一个tag，在consumerQueue中存储着tag的hashcode，因此通过对比hashcode可以很高效的过滤出来，对于过滤出来的消息，会完整的对比其tag内容，因此避免了hash冲突。
2. 通过SQL方式过滤，消耗CPU资源，增大磁盘压力，没有tag方式高效。
3. 通过Filter Server方式过滤，要使用这种方式，需要在配置文件中加上filterServer­Nums=3这样的配置，表示本地需要启动FilterServer进程，类似于consumer进程，只不过它是通过条件将消息拉取过滤，然后传送到远端的consumer。需要注意的是这种方式会占用broker CPU资源，而且上传的Java函数代码不能有申请大内存，创建线程等操作，不然会导致broker宕机（实现MessageFilter接口，实现match方式即可）。

**提高consumer的处理能力**

1. 提高消费并行度，通过增加consumer的实例数，或者加机器，注意consumer数量不要超过topic下的read queue的数量，超过consumer实例接收不到消息。此外，还可以增加单个consumer的并行处理能力来提高吞吐量（修改consumeThreadMin和consumeThreadMax参数）。
2. 批量消费的方式，设置consumer的consumeMessageBatchMaxSize这个参数。默认是1。
3. 检测延时情况，跳过不重要的消息，当消息发生堆积的时候，可以选择丢弃不重要的消息，使consumer尽快赶上producer的进度。

**消费端负载均衡**

当每个DefaultMQPushConsumer启动的时候，，马上会触发doRebalance动作。它有5中负载均衡策略。

DefaultMQPullConsumer，读取消息时的offset都是由使用者控制，用户可以实现自己的负载均衡方式，可以通过两个函数来辅助实现：

```java
Consumer.registerMessageQueueListener(”TOPICNAME”, new MessageQueueListener() {
public void MessageQueueChanged (String Topic, Set<MessageQueue> mqAll , Set<MessageQueue> mqDivided) )
```

registerMessageQueueListener函数在有新的Consumer加入或者退出的时候被触发，另一个函数式`MQPullConsumerScheduleService`类，使这个类的使用类似于DefaultMQPushConsumer，但是它把pull消息的主动性留给了使用者。

```Java
public static void main(String[) args) throws MQClientException {
f工nal MQPullConsumerScheduleService scheduleServ工ce = new MQPull- ConsumerScheduleService (”PullConsumerServicel ”);
scheduleService .getDefaultMQPullConsumer() setNamesrvAddr (”localh- ost : 9876”);
scheduleService.setMessageModel(MessageModel . CLUSTERING ) ;
......
```



### rocketmq消息的存储

每个topic下被分为messagequeue，若想读取整个topic中的数据，需要扫描所有的messagequeue，也可以指定messagequeue消费。messagequeue中的消息由CommitLog以及ConsumerQueue。实际数据存储在commitlog中，consumerQueue是一个

**查看网卡的状态：**

1. sar -n DEV 2 10
2. iperf3命令
3. netstat -t 查看网卡连接状态，是否有大量连接造成堵塞

  ​
  ​	
  ​		
  ​			
  ​					


​			
​		
​	

​	





