Spark Streaming使用WAL以及Checkpoint冷备的方式来进行容错。
WAL冷备是备份与后续工作相关的接收数据以及其块的元数据信息。
CheckPoint冷备是为了出错后恢复而做的备份。

设置参数`spark.streaming.receiver.writeAheadLog.enable`为true。

WAL日志的特点是顺序写入，所以在数据备份的时候效率很高，但是在需要恢复数据的时候也需要顺序读取，因此需要一定的恢复时间。

但是对于spark streaming来讲，恢复时很快，因为对于某个块的数据只有一次（新增块），不会对后续数据的追加，修改和删除操作进行记录，使得WAL中只有一条此块数据的日志入口，所以在恢复的时候找到这条日志的入口即可，不需要顺序读取整个个WAL。

Spark Streaming有自己的checkpoint机制。有以下两种：
1. Metadata Checkpoint：将元数据信息配置到可容错的分布式文件系统中。
2. Data Checkpoint：将生成的RDD保存到外部可靠的存储当中，对于一些数据跨度为多个batch的有状态 transformtaion操作来说，很有必要。

**热备份：**系统在正常工作的时候，在物理机上做好备用硬件运行所需要的状态的更新。遇到异常的时候，系统把受影响的工作自动切换到备用硬件上运行，以保证
继续不间断的进行。

**冷备份：**系统在正常运行的时候就做好日常数据和元数据的备份，遇到异常的时候，系统通常需要通过人工重新启动，并利用备份的数据和元数据来恢复工作。

Driver中使用了冷备方式。
Executor中使用了热备以及冷备的方式。
热备是指存储块数据的时候，将其存储到本Executor，同时复制到另一个Executor上去，当一个副本失效的时候，就可以
立刻感知切换到另一个副本来计算。

企业中现在更多使用的是NO Receiver的方式。

**exactly-once语义：**
Receiver方式有这样一个问题：Receiver接收数据是积累到一定的程度才会写入WAL，如果此时Receiver线程失败，那么数据就有可能会丢失。而Driect方式不会。

还要保证计算结果输出不重复。
对每一个partition会产生一个uniqueId，只有这个partition的数据被完成消费，才算成功，否则算失败，需要回滚，下次重复执行这个uniqueId，如果是执行过的则跳过，这样就保证了exactly-once语义。

spark streaming性能调优机制：
1. 数据接收的并行度：可以创建多个InputDstream，接收同一数据源数据，还可以通过配置，让这些DStream分别接收数据源的不同分区的数据。，最大DStream个数可以达到数据源提供的分区数。
JavaStreamingContext streamingContext=new JavaStreamingContext(conf,Durations.seconds(5));
 		//提高数据接收的并行度
		int numStreams=5;
		List<JavaPairDStream<String, String>> kafkaStreams=new ArrayList<JavaPairDStream<String,String>>();
		
		for(int i=0;i<numStreams;i++) {
//			kafkaStreams.add(KafkaUtils.createStream(arg0, arg1, arg2, arg3));
		}
		JavaPairDStream<String,String> unionDStream=streamingContext
				.union(kafkaStreams.get(0), kafkaStreams.subList(1, kafkaStreams.size()));

2. Task的并行度：
数据接收使用的BlockGenerator里面有个RecurringTimer类型的对象blockIntervalTimer，会周期性地发送BlockGenerator消息，进而周期性地生成和存储一个Block，这个周期有一个配置参数spark.streaming.blockInterval，这个时间周期的默认值为200ms。生成的一个个block，实际上就对应了RDD中提到的partition，每一个partition都会对应一个block，而spark streaming按照block Interval来组织一次数据接收和处理，所以Batch Interval内的block个数就是RDD的partition数，也就是RDD的并行Task数。

3. 序列化：使用kryo序列化方式或者自定义序列化接口。
4. 处理数据的速度要跟上数据流入的速度，即批处理时间必须小于批次间隔时间。
5. JVM GC：在Driver端以及Executor端开启使用CMS垃圾回收器。在spark-submit提交应用程序时执行时增加两个设置：
--driver-java-options"-XX:+UseConcMarkSweepGC"
--conf"spark.executor.extraJavaOptions=-XX:+UseConcMarkSweepGC"
