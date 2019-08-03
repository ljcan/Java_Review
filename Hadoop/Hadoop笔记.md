
YARN：分布式资源管理框架；管理整个集群的资源（内存，CPU核数）；分配调度集群的资源。

Namenode是主节点，存储文件的元数据如文件名，文件的目录结构，文件属性（生成时间，副本数，文件权限），以及每个文件的块列表和块所在的DataNode等。

Datanode在本地文件系统存储文件块数据，以及块数据的校验和。

SecondaryNameNode用来监控HDFS状态的辅助后台程序，每个一段时间来获取HDFS元数据的快照（合并镜像文件的编辑日志）。

https://www.cnblogs.com/cxzdy/p/5494929.html
YARN的架构图很人性化，client表示客户端，当client提交任务的时候，它会提交到ResourceManager，而在YARN的整个系统中，RM管理着所有的NodeManager，举个简单的例子，将YARN每一次提交的任务比作一个项目，RM相当于经理，NM相当于每一个模块的负责组，比如前后端等模块，在NM中会有一个container和APP master（RM创建的应用管理者），container相当于一个资源容器，不同的容器装着不同的请求所需要的资源，APP master会向RM发出请求，申请资源，然后去对应的container去获取对应的资源。






