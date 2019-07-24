HDFS的数据存储分为两块，一块是HDFS内存存储，一块是HDFS得异构存储。

**LAZY异步存储大概分为三个步骤：**
1. 对目标文件目录设置 StoragePolicy为 LAZY_PERSIST 的内存存储策略。
2. 客户端进程向 NameNode 发起创建/写文件的请求 。
3. 客户端请求到具体的DataNode后DataNode会把这些数据块写入RAM内存中，同时启动异步线程服务将内存数据持久化到磁盘中。
![HDFS懒加载](https://github.com/ljcan/jqBlogs/blob/master/Hadoop/HDFS%E5%86%85%E5%AD%98%E5%AD%98%E5%82%A8lazy.png)

**LAZY PERSIST内存存储**

FsDatasetlmpl，它是一个管理 DataNode所有磁盘读写的管家。
1. RamDiskAsyncLazyPersistService :此对象是异步持久化线程服务，针对每一个磁盘 块设置一个对应的线程池，需要持久化到给定磁盘的数据块会被提交到对应的线程池中去。每个线程池的最大线程数为1。
2. LazyWriter:这是一个线程服务， 此线程会不断地从数据块列表中取出数据块，将数 据块加入到异步持久化线程池 RamDiskAsyncLazyPersistService 中去执行 。
3. RamDiskReplicaLruTracker : 是副本块跟踪类， 此类中维护了所有己持久化 、未持久化的副本以及总副本数据信息。所以当一个副本被最终存储到内存中后，相应地会有副本所属队列信息的变更。当节点内存不足时，会将最近最少被访问的副本块移除。

**LAZY_PERSIST 内存存储的使用**

```
<property>
<name>dfs.datanode data .dir</name>
<Value>/grid/0 , /grid/l , /grid/2, [RAM_DISK) /mnt/dn-tmpfs</value>
</property>
```
HDFS默认是DISK。
1. 确保 HDFS 异构存储策略没有被关问，默认是开启的，配置项是是`dfs.storage.policy.enabled`。
2. 确认`dfs.datanode.max.locked.memory`是否设置了足够大的内存值，是否已是DataNode能承受的最大内存大小。内存值过小会导致内存中的总的可存储的数 据块变少，但如果超过DataNode能承受的最大内存大小的话,部分内存块会被直接移出。

**HDFS异构存储**

HDFS异构存储特性的出现使得我们不需要搭建2套独立的集群来存放冷热2类数据，在一套集群内就能完成。

以下是在HDFS中声名的Storage Type：
1. RAM_DISK
2. SSD
3. DISK
4. ARCHIVE

**异构存储的原理**

1. DataNode 通过心跳汇报自身数据存储目录的 StorageType 给 NameNode。
2. 随后 NameNode 进行汇总并更新集群内各个节点的存储类型情况 。
3. 待复制文件根据自身设定的存储策略信息向 NameNode 请求拥有此类型存储介质的DataNode作为候选节点。

**块存储策略集合**

1. HOT
2. COLD
3. WARM
4. ALL_SSD
5. ONE_SSD
6. LAZY_PERSIST

在这6种策略中，前三种策略和后三种策略可以看作是两大类。前三种策略是根据冷热数据的角度来区分的，后三种策略是根据存放盘的性质来区分的。

因为块有多副本机制，每个策略要为所有的副本都返回相应的StorageType(如`new Storageτ'ype[]{Storageτ'ype.SSD, Storageτ'ype .DISK}
`)，如果副本数超过候选的StorageType数组时应怎么处理：
1. 从前往后依次匹配存储类型与对应的副本下标相匹配，同时要过滤掉transient 属性的存储类型。
2. 获取最后一个存储类型，统一作为多余副本的存储类型。

以`new Storageτ'ype[]{Storageτ'ype.SSD, Storageτ'ype .DISK}`为例，第一个副本的类型必然是SSD，其余的副本跟最后一个类型一致，都是DISK类型。
![HDFS副本存储类型](https://github.com/ljcan/jqBlogs/blob/master/Hadoop/HDFS%E5%9D%97%E5%89%AF%E6%9C%AC%E5%AD%98%E5%82%A8%E7%B1%BB%E5%9E%8B.png)

**HDFS存储策略的使用**
```
hdfs storagepolicies -help
```

**HDFS短路读**

https://www.cnblogs.com/zhangningbo/p/4146296.html







