
YARN：分布式资源管理框架；管理整个集群的资源（内存，CPU核数）；分配调度集群的资源。

Namenode是主节点，存储文件的元数据如文件名，文件的目录结构，文件属性（生成时间，副本数，文件权限），以及每个文件的块列表和块所在的DataNode等。

Datanode在本地文件系统存储文件块数据，以及块数据的校验和。

SecondaryNameNode用来监控HDFS状态的辅助后台程序，每个一段时间来获取HDFS元数据的快照（合并镜像文件的编辑日志）。

https://www.cnblogs.com/cxzdy/p/5494929.html

YARN的架构图很人性化，client表示客户端，当client提交任务的时候，它会提交到ResourceManager，而在YARN的整个系统中，RM管理着所有的NodeManager，举个简单的例子，将YARN每一次提交的任务比作一个项目，RM相当于经理，NM相当于每一个模块的负责组，比如前后端等模块，在NM中会有一个container和APP master（RM创建的应用管理者），container相当于一个资源容器，不同的容器装着不同的请求所需要的资源，APP master会向RM发出请求，申请资源，然后去对应的container去获取对应的资源。

![MR on YARN](https://github.com/ljcan/jqBlogs/blob/master/Hadoop/MR%20on%20YARN.png)

**MapReduce on YARN流程图简述：**

当客户端提交任务到ResourceManager，ApplicationManager会去管理NodeManager生成一个App Master（应用管理者），当App Master建立之后，就会向ApplicationsManager来反馈并且调度，向ResouceScheduler调度资源，当调度完毕以后,App Master就会去管理相应的Node Manager，到容器Container中调度相应的任务，Map Task或者Reduce Task，在每一个任务运行的过程中，App Master还会一直监控着每一个任务，因此每一个任务会向其反馈当前任务的运行信息，最后，任务运行完毕后向ApplicationsManager提交任务，client还可以通过页面的信息来通过App Master监控任务的当前状况。

**DataNode数据校验**

当DataNode读取block的时候，它会计算checksum，如果计算后的checksum与block创建时值不一样，说明该block已经损坏，client读取其他DataNode上的block，然后删除该block，并且复制block副本数达到预期设置的文件备份数。DataNode在其文件创建三周后验证其checksum。



