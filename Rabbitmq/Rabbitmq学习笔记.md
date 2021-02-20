#### 安装

```shell
#安装erlang
yum install https://packages.erlang-solutions.com/erlang/rpm/centos/7/x86_64/esl-erlang_23.0-1~centos~7_amd64.rpm
#安装rabbitmq
yum install rabbitmq-server-3.8.9-1.el8.noarch.rpm
```

启动rabbitmq-server

```shell
systemctl start rabbitmq-server
systemctl status rabbitmq-server
```

安装web可视化界面

```
rabbitmq-plugins enable rabbitmq_management
#重启服务
systemctl restart rabbitmq-server

#1.修改配置文件/var/lib/rabbitmq/mnesia/rabbit@VM_216_159_centos-plugins-expand/rabbitmq_management-3.8.9/ebin

#2.添加新用户并且授予权限
rabbitmqctl add_user anroyliu anroyliu     #新建用户
rabbitmqctl set_user_tags anroyliu administrator    #设定用户administrator角色
rabbitmqctl set_permissions -p / anroyliu ".*" ".*" ".*"    #赋予用户权限


我们在b机上使用以下命令查看网络包转发情况，发现有掉包

iptables -t filter -nvL FORWARD
```

浏览器访问127.0.0.1:15672 ，用户名和密码都是guest



如何保证rabbitmq在崩溃中恢复不丢失消息呢？

1. durable设置为true，表示rabbitmq重启之后重新创建交换器和队列。

2. 投递模式，delivery mode设置为2

3. 持久化到交换器

4. 持久化到队列

   rabbitmq消息的持久化就是将消息写入磁盘的持久化日志文件中，当一条需要持久化的消息发送到交换器上，交换器会在成功接收到消息，并且写入持久化文件之后才会返回响应。之后，这条消息如果发送到一个非持久化的队列，那么这条消息将会从交换器的持久化日志文件中删除，因此，为了保证持久化消息不丢失，必须满足上述的几点要求。

   一旦rabbitmq从持久化队列中消费了这条消息，就会将这条消息标记为等待垃圾收集；如果消息被消费之前重启服务器的话，rabbitmq会自动重新创建交换器和队列，并且重播持久化消息到合适的交换器和队列中。

如何保证生产者发送成功消息给服务器了呢？

  AMQP事务，信道开启事务模式，可以保证生产者将消息发送给服务器并且写入磁盘成功再返回，但是对系统的吞吐量影响很大，是同步状态，在信道中，会有多个AMQP事务，当第一个执行成功的时候，其他的也会执行完成，如果第一个失败，后面的也就不会执行了。

  为了避免rabbitmq吞吐量的下降，可以使用发送方确认模式，将信道设置为confirm模式，信道会给发送给它的消息都设置一个唯一ID（从1开始），当有消息到达匹配的队列时，信道会发送一个确认消息给发送方，保证消息的成功到达，如果需要持久化，那么当消息写入磁盘才会返回成功消息，这样对系统的吞吐量影响很小，因为一旦一条消息发送，发送方在接收返回消息的同时，也会继续发送下一条消息，当收到消息会调用回调方法让发送方来处理该消息。如果发送失败，也会返回一条未确认的消息。



rabbitmq的每一个队列，交换器和绑定的元数据都是存储到mnesia中，他是erlang中内建非sql型数据库，mnesia首先将元数据追加到一个日志文件中，确保其完整性，然后定期将日志内容转储到真实的mnesia数据库文件中。mnesia中的参数dump_log_write_threshold选项控制着转储的频度，设置为1000，就表示每1000条就将数据从日志文件中转到数据库文件中。

查看队列名字，消息数目，消费者数目以及内存使用情况：

```
ls /usr/sbin/rabbitmqctl
./rabbitmqctl list_queues name messages consumers memory
```

查看交换器的信息：

```
./rabbitmqctl list_exchanges

[root@VM_216_159_centos /usr/sbin]# ./rabbitmqctl list_exchanges
Listing exchanges for vhost / ...
name    type
amq.fanout      fanout
amq.rabbitmq.trace      topic
amq.headers     headers
hello-exchange  direct
amq.topic       topic
amq.direct      direct
        direct            #匿名交换器，每个队列默认会绑定在该交换器下
amq.match       headers

./rabbitmqctl list_exchanges name type auto_delete durable
```

滚动日志（最后一个数字可以变化，代表后缀数字）：

```
./rabbitmqctl rotate_logs 1
```

erlang设置名称：

```
erl -sname xxx   #设置短名
1> net_adm:names()     #查看你机器上还存在哪些节点
```



rabbitmq之间跨服务复制数据使用shovel，在rabbitmq 2.7.0开始，rabbitmq-shovel和amqp_client插件随着rabbit一起打包，只需要开启他们即可。

```
rabbitmq-plugins enable amqp_client
rabbitmq_plugins enable rabbitmq_shovel
```



如果想要完全的高可用性，并且不丢失任何消息，就可以使用两台独立的rabbitmq服务器来搭建warren，并用负载均衡使得他们对应的程序想单个实体一样。

在1.8.0版本之前，当包含持久化队列的集群节点发生故障的时候，如果客户端重新创建了持久化队列，那么节点恢复时，旧队列中的内容会丢失。

在1.8.0之后，当拥有持久化队列的节点发生故障的时候，该队列无法被重新创建，任何尝试重新声明队列的客户端都会收到一个404 NOT_FOUND AMQP错误，当故障节点恢复，持久化队列及其内容也会跟着恢复。但是在接节点恢复以前，任何应该投递到该队列的数据要么丢失了，要么由于设置了mandatory发布标识导致客户端收到了错误。

因此使用warren模式来实现无共享架构，前置一台负载均衡起做故障转移，warren指一对主备独立服务器，主备服务器之间没有协作，所以影响到主服务器的问题不会转移到备用服务器上。当主节点发生故障的时候，可以把备当做重新发布和消费消息的地方，当主节点恢复时，它允许你的消费者重新连接并且消费主节点发生故障时队列上的那些消息。所以不会丢失任何老的或者新的数据，但是必须等待主节点恢复之后旧消息才能重新变为可用。

**http restful api**

```
$ curl -i -u anroyliu:anroyliu http://xxxx:15672/api/queues/rabbit_VM_216_159_centos_2020/ping
HTTP/1.1 404 Not Found
content-length: 49
content-security-policy: script-src 'self' 'unsafe-eval' 'unsafe-inline'; object-src 'self'
content-type: application/json
date: Thu, 10 Dec 2020 03:19:24 GMT
server: Cowboy
vary: accept, accept-encoding, origin

{"error":"Object Not Found","reason":"Not Found"
```

**消息持久化**

声明队列时, `Durability` 的属性值, 可以为 `durable` (持久的) 和 `transient` (短暂的).

- durable : 表示 RabbitMQ 重启后, 依然会存在的队列(MQ 会自动重新声明这个队列, 即有持久化这个队列的声明)

- transient : 表示 RabbitMQ 重启后, 不会存在的队列(MQ不会自动声明了, 即没有持久化这个队列的声明属性)​


投递消息时, 也有个属性: `Delivery mode` , 可以为 `Persistent` 和 `Non-Persistent` .

- persistent : 只要消息一达到队列, 就会立即写到文件来持久化
- non-persistent: 它只会在 rabbitmq 存在内存压力的时候, 才会持久化到磁盘.

```
delivery-mode 设置为1非持久化，设置为2持久化
```

**消息确认**

```
no-ack设置为true表示客户端接收到消息后不需要向服务端回复确认消息；否则，该消息会被添加到pending-acknowledgment列表中用来记录消息。
```

**消息投递**

```
mandatory和immediate标记设置为false的话，那么这个过程会以异步的方式执行，从客户端的角度来看，服务器会变得更快。

如果mandatory参数设置为true，那么生产者发送消息给rabbitmq，如果找不到符合条件绑定的队列，那么会通过basic.return返回给生产者，否则直接丢弃。
rabbitmq 3.0之后去掉了immediate这个参数。
```

如果消息投递的目的队列时空的并且消费者已经准备好接受消息的话，那么消息会直接发送给消费者，而不会经过队列这一步，极大地提升消息投递的速度。

如果消息投递的队列不为空的时候，那么消息会入队，如果消息不是持久化的，它会被保存在内存中，如果内存不足，消息被写入磁盘。

**erlang进程计数**

rabbitmq的默认设置是每个erlang节点1048576，即2的20次方。erlang应用程序在整个生命周期中会多次创建并且销毁进程。当rabbitmq接收到AMQP客户端的TCP连接时，就会创建一个erlang进程来管理该连接。同时，会有许多erlang进程来处理rabbitmq消息存储的逻辑。另一些进程会监控子进程来确保它们正常运行。一般来说，大概会有一百多个进程在工作。

**消息消费**

当多个消费者同时订阅一个队列的时候，这时队列中的消息会被平均分摊（Round-Robin，即轮询）。

**交换器类型**

1. fanout：会把所有的发送到该交换器的消息路由到该交换器绑定的队列中。
2. direct：将消息中的`Routing key`与该`Exchange`关联的所有`Binding`中的`Routing key`进行比较，如果相等，则发送到该`Binding`对应的`Queue`中。
3. topic：将消息中的`Routing key`与该`Exchange`关联的所有`Binding`中的`Routing key`进行对比，如果匹配上了，则发送到该`Binding`对应的`Queue`中。
4. headers : 将消息中的`headers`与该`Exchange`相关联的所有`Binging`中的参数进行匹配，如果匹配上了，则发送到该`Binding`对应的`Queue`中。



**无论生产者还是消费者和rabbitmq连接都首先要创建一个连接（TCP连接），然后再创建一个信道（channel），目的是为了防止多个线程连接rabbit的时候创建大量的连接，消耗资源，成为系统瓶颈，从而达到TCP复用的目的。当每个信道本身流量很大的时候，就需要进行调优，创建多个连接，将请求分摊到这些连接上。**



#### Java api

**生产者**

```Java
/** 
exclusive 参数设置为是否排他，为true则设置为排他的，如果一个人队列为设置为排他队列，那么该队列对首次声明它的连接可见，并且在连接断开的时候自动删除。 1.排他队列时基于连接可见的，也就是一个连接下的不同channel都可以访问，但是不同连接不能访问。2.如果一个连接声明了一个排他队列，那么其他连接是不允许创建同名的排他队列的。3.即使该队列时持久化的，一旦连接关闭或者客户端退出，该排他队列会被自动删除，这种队列适用于一个客户端同时发送和读取消息的应用场景。 

autoDelete：自动删除，如果该队列没有任何订阅的消费者的话，该队列会被自动删除。这种队列适用于临时队列。
*/
Queue.DeclareOk queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments) throws IOException;

//不需要等待服务器的返回，即客户端不知道服务端是否声明创建队列成功
void queueDeclareNoWait(String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments) throws IOException;
//清空队列的内容，区别于queueDelete，不是删除队列本身
 Queue.PurgeOk queuePurge(String queue) throws IOException;
```

**消费者**

rabbitmq消费模式分为两种：推（push）模式和拉（pull）模式，推模式采用Basic.Consume进行消费，而拉模式则是调用Basic.Get进行消费。

1. 推模式

```Java
//noLocal参数设置为true则表示不能将同一个connection中生产者发送的消息传送给这个connection中的消费者
String basicConsume(String queue, boolean autoAck, String consumerTag, boolean noLocal, boolean exclusive, Map<String, Object> arguments, Consumer callback) throws IOException;
```

2. 拉模式

  通过`channel.basicGet`方法可以单条地获取消息，返回值为`GetResponse`

```Java
GetResponse basicGet(String queue, boolean autoAck) throws IOException;
```

如果消费者不想接收当前消息，可以拒绝接收：

```java
//deliveryTag可以看作消息的编号，它是一个64位长整形数值，如果requeue为true，则表示消息重新放入队列中，以便可以发送给下一个消费者，为false，表示直接丢弃
//该方法一次只可以拒绝一条消息
void basicReject(long deliveryTag, boolean requeue) throws IOException;
//批量拒绝消息、multiple为false表示丢弃编号为deliveryTag的这一条消息，为true则表示要丢弃多条消息，拒绝deliveryTag编号之前所有没有被消费者确认的消息
void basicNack(long deliveryTag, boolean multiple, boolean requeue) throws IOException;
//该方法用来请求MQ重新发送还没有被确认的消息、如果requeue为true，则表示未被确认的消息会重新加入队列中，这样对于同一条消息可能会被分配到不同的消费者，如果为false，那么同一条消息会分配给与之前相同的消费者，该参数默认为true
Basic.RecoverOk basicRecover(boolean requeue) throws IOException;
```

**备份交换器**

```Java
/**
 * 备份交换器防止数据丢失
 */
public class AlternateProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        Map<String,Object> argsMap = new HashMap<>();
        argsMap.put("alternate-exchange","myAe");
        channel.exchangeDeclare("normal-exchange","direct",true,false,argsMap);
        channel.exchangeDeclare("myAe","fanout",true,false,null);
        channel.queueDeclare("normal-queue",true,true,false,null);
        channel.queueBind("normal-queue","normal-exchange","normal-key");
        channel.queueDeclare("ae-queue",true,true,false,null);
        channel.queueBind("ae-queue","myAe","");
        channel.basicPublish("normal-exchange","ae-key",
                MessageProperties.PERSISTENT_TEXT_PLAIN,"test ae message".getBytes());
        System.out.println("发送消息测试备份交换器，若是queue没有与exchange匹配绑定的key，那么将会把消息发送到备份交换器");
    }
}
```

**设置消息的TTL**

```Java
//既可以设置队列消息的过期时间，也可以设置单条消息的过期时间，如果两者同时设置，则以两者中值最小的那一个为消息的过期时间。设置TTL为0，则表示除非消息会直接发送到消费者，否则消息过期被丢弃 
Map<String,Object> queueMap = new HashMap<>();
 queueMap.put("x-message-ttl",100);    //设置队列消息的过期时间，单位为毫秒
 channel.queueDeclare("normal-queue",true,true,false,queueMap);

//设置消息的属性
            channel.basicPublish("exchangeName","routingKey",
                    new AMQP.BasicProperties().builder()
                            .contentType("text/plain")
            .deliveryMode(2)
             .expiration("200")    //设置单条消息的过期时间
            .priority(1)
            .userId("")
            .build(),"messageBytes".getBytes());
```

**设置队列的TTL**

通过在`channel.queueDeclare`方法中的`x-expires`参数可以控制队列的生存时间，即在多少毫秒内如何未使用该队列，那么队列会被删除，在MQ重启后，持久化的队列的过期时间会被重新计算。该值不能设置为0。

**死信队列**

DLX，全称Dead-Letter-Exchange，可以称之为死信交换器，当一个消息变成死信之后，它就会被重新发送到另一个交换器中，这个交换器就是DLX，绑定的队列就是死信队列。消息一般是下面几种情况下变成死信：

1. 消息被拒绝、并且设置`requeue`参数为false。
2. 消息过期。
3. 队列达到最大长度。

```java
/**
 * 死信队列
 */
public class DeadLetterProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("exchange.normal","direct",true,false,null);
        channel.exchangeDeclare("exchange.dead","fanout",true);
        Map<String,Object> map = new HashMap<>();
        map.put("x-message-ttl",10000);       //消息10秒过期
        map.put("x-dead-letter-exchange","exchange.dead");    //设置死信交换器
        map.put("x-dead-letter-routing-key","routingkey");
        channel.queueDeclare("queue.normal",true,false,false,map);
        channel.queueDeclare("queue.dead",true,false,false,null);
        channel.queueBind("queue.normal","exchange.normal","normal");
        channel.queueBind("queue.dead","exchange.dead","routingkey");
        channel.basicPublish("exchange.normal","normal",
                MessageProperties.PERSISTENT_TEXT_PLAIN,"dead message".getBytes());

    }
}
```

通过订阅死信队列，也可以实现延迟队列，`x-message-ttl`即为延迟的时间。

**优先级队列**

通过参数`x-max-priority`参数来设置，值为整数。优先级高的队列具备优先被消费的特权。

在发送消息的时候，可以通过`builder.priority(5)`来设置消息当前的优先级。

**持久化**

消息的持久化：`BasicProperties`中的`deliveryMode`属性设置为2即可实现消息的持久化。`MessageProperties.PERSISTENT TEXT PLAIN `实际上是封装了这个属性:



**生产者确认**

1.使用事务机制防止生产消息没有到达MQ，致使消息丢失，事务是同步机制，对MQ性能影响比较大：、

```java
  try{
            channel.txSelect();
            channel.basicPublish();
            int result =1/0;
            channel.txCommit()
        }catch (Exception e){
            e.printStackTrace();
            channel.txRollback();
        }
```

2.使用发送端确认方式，该方式是异步方式，对于发送到信道的每一条消息，会生成一个唯一ID，一旦接受到消息，MQ会发送一个确认（Basic.Ack）给生产者，包含这个唯一ID，如果消息是持久化的，那么会等到消息写入磁盘之后。还可以设置`channel.basicAck()方法中的multiple参数`，表示到这个序号之前得所有消息都已经得到了处理。

```Java
 //发送方确认模式
        channel.confirmSelect();
        channel.basicPublish();
        if(!channel.waitForConfirms()){
            System.out.println("send message failed!");
        }
```

`channel.confirmSelect()`方法将信道设置为confirm模式，所有被发送的后续消息都会被ack或者nack一次，不会出现一条消息既被ack又被nack的情况，并且rabbitmq并没有对消息的confirm的快慢做出保证。

上面代码如果每发送一条消息就调用`waitForConfirms`方法的话和同步没啥区别，性能提高不多，因为可以在批量发送消息之后再调用该方法，但是批量发送消息可能会导致出现返回`Basic.Nack`或或者超时的情况，所以这时可以将发送的每一条消息存入本地缓存中，当达到一定的数量之后，再调用该方法，如果返回异常，则将缓存的数据重新发送，返回正常则可清空缓存数据。

3. 异步confirm方式：

   ```java
   /**
    * 异步confirm
    */
   public class ConfirmProducer {
       public static void main(String[] args) throws Exception{
           ConnectionFactory factory = new ConnectionFactory();
           Connection connection = factory.newConnection();
           Channel channel = connection.createChannel();

           channel.confirmSelect();
           TreeSet<Long> confirmSet = new TreeSet<>();
           channel.addConfirmListener(new ConfirmListener() {
               @Override
               public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                   System.out.println("消息发送成功，deliveryTag:"+deliveryTag+" multiple："+multiple);
                   if(multiple){     //删除多条数据，即当前数据之前得所有数据已经被提交
                       confirmSet.headSet(deliveryTag-1).clear();
                   }else {     //删除一条数据
                       confirmSet.remove(deliveryTag);
                   }
               }
               @Override
               public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                   System.out.println("消息发送异常，deliveryTag:"+deliveryTag+" multiple："+multiple);
                   if(multiple){     //删除多条数据，即当前数据之前得所有数据已经被提交
                       confirmSet.headSet(deliveryTag-1).clear();
                   }else {     //删除一条数据
                       confirmSet.remove(deliveryTag);
                   }
               }
           });
           while (true){      //模拟一直发送消息
               long nextNo = channel.getNextPublishSeqNo();
               channel.basicPublish("confirm-exchange","confirm",
                       MessageProperties.PERSISTENT_TEXT_PLAIN,("test message "+nextNo).getBytes());
               confirmSet.add(nextNo);
           }
       }
   }
   ```



**消费端**

如果消费端有多个消费者同时消费MQ的时候，那么消息会轮询发送到这些消费者上，为了防止因为消费端的性能差异导致有的消息来不及消费，可以在消费端程序调用`channel.basicQos(5)`，这样，每发送一条消息都会为对应的消费者计数，如果达到了所设定的上限，那么MQ就不会向这个消费者再发送消息，直到消费者确认了这些消息知乎，MQ将相应的计数减1，然后继续接受消息，类似于TCP中的滑动窗口。tips：这种方式对于拉模式的消费者无效。



**单点故障**

如果集群节点因为硬盘，内存，主板等故障造成死机，那么需要在集群中的其他节点来执行以下命令来将故障节点剔除：

```Shell
rabbitmqctl forget_cluster_node {nodename}
```

判断集群中还有哪些队列还有消息没被消费掉：

```Shell
sh /usr/sbin/rabbitmqctl list_queues -p /|awk '{if($2>0) print $0,$1}'
```

**存储机制**

对于rabbitmq的消息的存储分为持久化消息与非持久化消息的存储，这些操作是在持久层完成。

持久层是一个逻辑的概念，实际上包括两个部分：队列索引（rabbit_queue_index）和消息存储（rabbit_msg_store）。rabbit_queue_index负责维护队列中落盘消息的信息，包括消息的存储地点，是否被交付给消费者，是否被消费者ACK等，每个队列都有与之对应的队列索引。 Rabbit_msg_store以键值对的形式存储消息，被所有队列共享，每个节点有且只有一个。从技术层面上说，rabbit_msg_store具体分为msg_store_persistent和msg_store_transient，前者负责持久化消息的持久化，重启后消息不会丢失，后者负责非持久化消息的持久化，重启后消息会丢失。

较小的消息会存储在rabbit_queue_index中，较大的消息存储在rabbit_msg_store中，这个消息的大小界定可以通过queue_index_embed_msgs_below来配置，默认大小为4096B，这里的大小是指消息体、属性及headers整体的大小。

rabbitmq会在ETS（erlang term storage）表中记录消息在文件中的位置映射（index）和文件的相关信息。

当读取消息的时候，先根据消息的id(msg_Id)找到对应的存储的文件，如果文件存在并且未被锁住，则直接打开文件，从指定位置读取消息的内容。如果文件不存在或者被锁住了，则发送请求由rabbit_msg_store进行处理。

消息的删除只是从ETS表中删除指定消息的相关信息，同时更新消息对应的存储文件的相关信息。执行消息删除的操作时，并不立即对在文件中的消息进行删除，也就是消息依然在文件中存在，仅仅标识为垃圾数据而已。当一个文件中都是垃圾数据时可以将这个文件删除。当检测到前后两个文件中的有效数据可以合并在一个文件中，并且所有的垃圾数据大小和所有文件的数据大小的比值超过设置的阀值GARBAGE_FRACTION（默认0.5）时才会触发垃圾回收将两个文件合并。

合并的两个文件一定是逻辑上相邻的文件，执行合并时先锁定这两个文件，先对前面的文件的有效数据进行整理，再将后面文件的有效数据写入到前面的文件，同时更新ETS表，最后删除文件。

**队列的结构**

队列通常由rabbit_amqqueue_process和backing_queue两部分组成，rabbit-amqqueue_process负责协议相关的消息处理，即接收生产者发布的消息，像3消费者交付消息，处理消息的确认等。backing_queue是消息存储的具体形式和引擎，并向rabbit_amqqueue_process提供相关的接口以供调用。队列中的消息有以下4种状态：

1. alpha：消息内容（包括消息体，属性和headers）和消息索引都存储在内存中。
2. beta：消息内容保存在磁盘中，消息索引保存在内存中。
3. gamma：消息内容保存在磁盘中，消息索引在磁盘和内存中都有。
4. delta：消息内容和索引都在磁盘中。

rabbitmq会在运行时根据统计的消息传送速度定期计算一个当前内存中能够保存的最大消息数量（target_ram_count），那么多余的消息会在这些状态之间转换。对于普通的没有设置优先级和镜像的队列来说，baking_queue的默认实现是rabbit_variable_queue，其内部通过5个子队列Q1,Q2,Delta,Q3,Q4来体现消息的各个状态。一般情况下，消息会根据系统负载的情况按照Q1->Q2->Delta->Q3->Q4这样的顺序步骤进行流动，从内存到磁盘，再从磁盘到内存的一个状态。使得整个队列具有很好的弹性。

在系统负载较高的时候，已经接受的消息若不能及时被消费掉，那么这些消息就会进入到很深的队列中去，这样会增加处理每个消息的平均开销。因为要花更多的时间和资源处理”堆积“的消息，如此用来处理新流入的消息的能力就会降低，使得后流入的消息会被积压到很深的子队列中，继续增大每个消息的处理平均开销，使得系统的处理能力大大的降低。一般有下面3中措施：

1. 增加prefetch_count的值，即一次发送多条消息给消费者，加快消息被消费的速度。
2. 采用multiple ack，降低处理ack带来的开销。
3. 流量控制。

**惰性队列**

rabbitmq从3.6.0版本开始引入了惰性队列，惰性队列会尽可能的将消息存入磁盘中，而在消费者消费到相应的消息时才会被加载到内存中，它的一个重要设计目标是可以支持更长的队列，即支持更多的消息存储。当消费者由于各种原因致使长时间不能消费而造成堆积时，惰性队列就很有必要了。

设置惰性队列：

1. 调用channel.queueDeclare方法的时候在参数中设置，优先级更高。
2. 在队列声明的时候设置参数`x-queue-mode`来设置队列的模式，取值为lazy和default。

**内存告警**

如果内存使用超过了规定的大小，那么会产生内存告警，默认情况下是`vm_memory_high_watermark`的值为0.4。

在内存触及阀值之前会尝试将队列中的消息换页到磁盘以释放内存空间。持久化和非持久化的数据都会被转储到磁盘，其中持久化的消息本身就在磁盘中有一份副本，这里会将持久化的消息从内存中清除掉。默认情况下，在内存到达内存阀值的50%的时候就会进行换页动作，也就是说，在默认的内存阀值为0.4的情况下，当内存超过0.4*0.5=0.2的时候就会进行换页动作。可以通过参数`vm_memory_high_watermark_paging_ratio`来设置。

`disk_free_limit`项来设置磁盘阀值

**流控**

rabbitmq使用的是一种基于信用证算法的流控机制来限制发送消息的速率来解决因erlang进程的消息堆积导致内存溢出而使服务崩溃。

该算法通过监控各个进程的进程邮箱，当某个进程负载过高而来不及处理消息的时候，这个进程的进程邮箱就开始堆积消息，当堆积到一定的量，就会阻塞而不接收新消息，从而慢慢地上游进程也可以堆积消息，最后就会使得负责网络数据包接收的进程阻塞而暂停接收新的数据。

**性能优化**

开启erlang语言的HIPE功能：https://www.cnblogs.com/me-sa/archive/2012/10/09/erlang_hipe.html



镜像队列同时支持publisher confirm和事务两种机制，在事务机制中，只有当前事务在全部镜像中执行之后，客户端才会收到Tx.Commit-OK的消息。同样的，在publisher confirm机制中，生产者进行当前消息确认的前提是该消息被全部进行所接收了。

镜像队列的结构中，master的backing_queue采用的是rabbit_mirro_queue_master，而slave的backing_queue实现是rabbit_mirro_queue_slave。所有对rabbit_mirror_queue_master的操作都会通过组播GM的方式同步到各个slave中，GM负责消息的广播，rabbit_mirror_queue_slave负责回调处理，而master上的回调由coordinator负责完成，除了Basic.publish，所有的操作都是通过master来完成的，master对消息进行处理的同时将消息的处理通过GM广播给所有的slave，slave的GM收到消息后，通过回调交由rabbit_mirror_queue_slave进行实际的处理。

GM模块实现的是一种可靠的组播通信协议，该协议能够保证组播消息的原子性，即保证组中活着的节点要么都收到消息要么都收不到，它的实现大致为：将所有的节点形成一个循环链表，每个节点都会监控位于自己左右两边的节点，当有节点新增时，相邻的节点保证当前广播的消息会复制到新的节点；当有节点失效的时候，相邻的节点会接管以保证本次广播的消息会复制到所有的节点。在master和slave上这些GM形成一个组（gm_group），这个组的信息会记录到mnesia中，不同的镜像队列形成不同的组。操作命令从master对应的GM发出后，顺着链表传送到所有的节点。由于所有节点组成了一个循环链表，master对应的GM最终会收到自己发送的操作命令，这个master就知道该操作命令都同步到了所有的slave上。

1. 当slave挂掉之后，除了slave相连的客户端连接全部断开，没有其他影响。
2. master挂掉之后，除了与他相连的客户端全部断开，选举最老的slave作为新的master，因为最老的slave与旧的master之间的同步状态应该是最好的。如果此时所有slave处于未同步状态，则未同步消息丢失。
3. 新的master会重新入队所有的unack的消息。此时客户端可能会有重复的消息。
4. 如果客户端连接着slave，并且Basic.Consume消费时指定了`x-cancel-on-ha-failover`参数，那么断开之时客户端会收到一个Consumer Cancellation Notification的通知，消费者客户端中会回调Consumer接口的handleCancel方法，如果没有指定该参数，那么消费者将无法感知master宕机。

镜像队列的配置：

`ha-mode`：指明镜像队列的模式，有效值为all、exactly、nodes，默认为all。all表示在集群中所有的节点上进行镜像；exactly表示在指定个数的节点上进行镜像；nodes表示在指定节点上进行镜像。该参数对排他队列不生效

`ha-sync-mode`：队列中消息的同步方式，有效值为automatic和manual。当新加入的slave设置为manual的时候，镜像队列中的消息不会主动同步到新的slave中，除非显式调用同步命令。当设置为automatic时，会主动同步数据。

如果master是使用命令主动停掉，那么slave不会接管，如果是因为故障被迫宕机，那么slave会接管master成为新的master。











































