HDFS的数据存储分为两块，一块是HDFS内存存储，一块是HDFS得异构存储。

**异构存储大概分为三个步骤：**
1. 对目标文件目录设置 StoragePolicy为 LAZY_PERSIST 的内存存储策略。
2. 客户端进程向 NameNode 发起创建/写文件的请求 。
3. 客户端请求到具体的DataNode后DataNode会把这些数据块写入RAM内存中，同时启动异步线程服务将内存数据持久化到磁盘中。

![HDFS懒加载](https://github.com/ljcan/jqBlogs/blob/master/Hadoop/HDFS%E5%86%85%E5%AD%98%E5%AD%98%E5%82%A8lazy.png)
