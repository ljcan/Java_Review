1. fetch.min.bytes：该属性指定了消费者获取broker中记录的最小数据。消费者在请求broker中的数据时，如果数据没有达到该参数指定的大小，那么它会等到攒够了
足够数据才会发送给消费者。适当调大改参数的值可以减轻消费者和broker的负载。
2. fetch.max.wait.ms：fetch.min.bytes指定broker等到有足够数据发送给消费者，而该参数设置了消费者最多等待多长时间。因此消费者获取的数据需要看这两个参数哪个先到达，如果攒够了fetch.min.bytes值大小的数据，那么将这些数据发送给消费者，否则将等待时间间隔后的所有数据发送给消费者。
3. max.paritition.fetch.bytes：该参数指定了服务器从每个分区里返回给消费者的最大字节数，默认是1MB。
4. session.timeout.ms：该参数指定了消费者被认为死亡之前可以与服务器断开连接的时间。默认是3s。该参数与heartbeat.interval.ms密切相关，并且必须比session.timeout.ms小，一般是其三分之一。
5. auto.offset.reset：该参数指定了如果消费者在读取了一个没有偏移量或偏移量无效的分区时该如果处理。默认值为latest，指从最新的偏移量开始消费数据。另一个值为earliest，指偏移量无效的情况下从分区的起始位置开始消费数据。
6. enable.auto.commit：该参数指定了消费者是否自动提交消费偏移量。默认值为true。一般情况下为了防止数据丢失和重复，设置为false。如果将其设置为true，可以通过auto.commit.interval.ms来控制提交频率。
7. partition.assignment.strategy：该参数指定了分区分配给消费者的策略。有两个值可以设置，RangeAssignor和RoundRobinAssignor。默认为RangeAssignor。
8. client.id：该参数标识从客户端发送过来的消息。
9. max.poll.records：该参数设置单词调用call()方法能够返回的记录数量。
10. receive.buffer.bytes和send.buffer.bytes：指定读写时使用的TCP缓冲区的大小，默认值为-1，即使用TCP的默认值。
