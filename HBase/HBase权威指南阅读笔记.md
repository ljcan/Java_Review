
Region是分布式存储的最小单元，store是存储的最小单元，Region是由一个或者多个store组成，每个store保存一个columns family。
每个store由一个memStore和0至多个StoreFile组成。memStore存储在内存中，StoreFile存储在HDFS上。

**HBase的写入流程：**
1. 手首先写入日志文件的WAL中（防止RS挂掉）。
2. 然后写入内存中memstore（缓存溢出则刷入磁盘，写入缓存的目的是顺序写数据）。
3.写入HDFS文件storefile。
4.当写入增长到一个阀值，将多个storefile合并为一个大的文件，同时进行版本合并和数据的删除。（HBASE在数据写入和删除的时候不会立即执行，数据写入内存即可返回，删除的时候会为删除的数据标记墓志铭，等待compact的时候真正执行，因此保证了操作的实时性）。
5.当单个storeFile大小超过一定的阀值的时候，就会执行split操作，将当前region split成2个region。region会下线，新的split出的两个孩子region会被
hmaster分配到相应的和regionserver上，使得原先1个region的压力得以分流到2个region上。

**HBASE读取数据：**
从ZK中存储的meta table中获取RS的信息，找到RS之后，进入meta表中去检索需要的数据，首先寻找该数据所在表的该区域的region存储的RS，找到再找是哪一个
region中，然后寻找rowkey，最后找到该数据。

根据CAP理论，在一个分布式系统中，在产生分区的情况下，不能同时满足高可用和一致性，所以对于HBase来说，每行数仅仅由一台服务器所服务，因此HBase具有
强一致性，当master挂掉之后

1.HBase在设计上完全避免了显示的锁，提供了行原子性操作，这使得系统不会因为读写操作性能而影响系统扩展能力。当前的列式存储结构允许表在实际存储时不存储NULL
值，因为表可以看做是一个无线的、稀疏的表。表中的每行数据只由一台服务器所服务，因此HBase具有强一致性，使用多版本可以避免因并发解耦过程引起的编辑冲突，而且
可以保留这一行的历史变化。

