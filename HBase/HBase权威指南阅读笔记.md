
Region是分布式存储的最小单元，store是存储的最小单元，Region是由一个或者多个store组成，每个store保存一个columns family。
每个store由一个memStore和0至多个StoreFile组成。memStore存储在内存中，StoreFile存储在HDFS上。

**HBase的写入流程：**
1. 手首先写入日志文件的WAL中（防止RS挂掉）。
2. 然后写入内存中memstore（缓存溢出则刷入磁盘，写入缓存的目的是顺序写数据）。
3.写入HDFS文件storefile。
4.当写入增长到一个阀值，将多个storefile合并为一个大的文件，同时进行版本合并和数据的删除。（HBASE在数据写入和删除的时候不会立即执行，数据写入内存即可返回，删除的时候会为删除的数据标记墓志铭，等待compact的时候真正执行，因此保证了操作的实时性）。
5.当单个storeFile大小超过一定的阀值的时候，就会执行split操作，将当前region split成2个region。region会下线，新的split出的两个孩子region会被
hmaster分配到相应的和regionserver上，使得原先1个region的压力得以分流到2个region上。

HBASE中的WAL日志是在每一个RS中一份，而不是每一个region中，这样不同的region会混在一起（可能来自不同table），这样做的目的是为了追加一个文件而不是多个
文件，减少磁盘寻址次数，因为提高了对table的写性能。不过当一台HRegionServer下线时，为了恢复其上的region，需要将HRS上的log进行拆分，然后发到其他HRegionServer上进行恢复。

**HBASE读取数据：**
从ZK中存储的meta table中获取RS的信息，找到RS之后，进入meta表中去检索需要的数据，首先寻找该数据所在表的该区域的region存储的RS，找到再找是哪一个
region中，然后寻找rowkey，最后找到该数据。在每一个region中，先去memstore中查询，查不到就到BlockCache中查询，再查不到就会到磁盘上读取数据，
并把读的结果放入BlockCache，BlockCache采用的是LRU策略。

在一些注重读的场景下，可以将BlockCache设置大些，Memstore设置小些，以加大缓存的命中率。

**BlockCache**

将cache进行分级：对于整个BlockCache的内存，按照以下百分比分配给single，multi，inMemory使用：0.25、0.50和0.25。
其中InMemory队列用于保存HBase Meta表元数据信息，因此如果将数据量很大的用户表设置为InMemory的话，可能会导致Meta表缓存失效，进而对整个集群的性能
产生影响。

首先，通过inMemory类型cache，可以有选择地将in-memory的column families放到RS内存中，例如meta元数据信息。
通过区分single和multi类型cache，可以防止由于scan操作带来的cache频繁颠簸，将最少使用的block加入到淘汰算法中。

根据CAP理论，在一个分布式系统中，在产生分区的情况下，不能同时满足高可用和一致性，所以对于HBase来说，每行数仅仅由一台服务器所服务，因此HBase具有
强一致性，当master挂掉之后

1.HBase在设计上完全避免了显示的锁，提供了行原子性操作，这使得系统不会因为读写操作性能而影响系统扩展能力。当前的列式存储结构允许表在实际存储时不存储NULL
值，因为表可以看做是一个无线的、稀疏的表。表中的每行数据只由一台服务器所服务，因此HBase具有强一致性，使用多版本可以避免因并发解耦过程引起的编辑冲突，而且
可以保留这一行的历史变化。



**Client**

整个HBase集群的访问入口；使用HBase的RPC机制与HMaster和HRegionServer进行通信；与HMaster进行通信进行管理类操作；
与HRegionServer进行数据读写类操作；包含访问HBase的接口，并维护cache来加快对HBase的访问。

Client访问HBase上数据的过程并不需要master参与，寻址访问ZK和HRegion Server，数据读写访问HRS。HMaster仅仅维护table和region的元数据信息，负载很低。


**表的设计**
1. 表的预分区
2. 表rowkey的设计，索引表。

**rowkey的设计**
1. 依据rowkey查询最快。
2. 对rowkey进行范围查询range。
3. 前缀匹配。

**LSM树**

LSM树的设计思想非常朴素：将对数据的修改增量保持在内存中，达到指定的大小限制后将这些修改操作批量写入磁盘，不过读取的时候稍微麻烦，需要合并磁盘中历史数据和内存中最近修改操作，所以写入性能大大提升，读取时可能需要先看是否命中内存，否则需要访问较多的磁盘文件。极端的说，基于LSM树实现的HBase的写性能比Mysql高了一个数量级，读性能低了一个数量级。



