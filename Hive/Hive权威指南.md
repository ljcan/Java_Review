### 二、基础操作
**hive中使用一次性命令**

可以使用参数-e来执行，不需要进入命令行。
eg:`hive -e "SELECT * FROM mytable LIMIT 3";`

有时可能需要将查询结果保存到一个文件中，增加-S选项可以开启静默模式，这样可以在输出结果中去掉“OK”等无关紧要的信息。
eg:`hive -S -e "select * FROM mytable LIMIT 3" > /tmp/myquery`

**从文件中执行Hive查询**

hive中可以使用-f参数执行某一个hive查询文件，文件必须以.q或者.hql后缀名结尾。

**查看历史操作**
Hive会将最近10000行命令记录到文件$HOME/.hivehistory中。

**执行shell命令**
用户不需要退出hive CLI就可以执行简单的bash shell命令，只要在命令前加上!并且以;结尾就可以了。

hive CLI中也可以执行Hadoop命令，去掉hadoop/hdfs执行剩余命令即可。

### 三、数据类型与文件格式

**基本数据类型**


| 数据类型 | 长度 |
| ------- | -------------|
| TINYINT | 1byte有符号整数
| SMALINT |  2byte有符号整数
| INT    |4byte有符号整数
| BIGINT  | 8byte有符号整数
| BOOLEAN  | 布尔类型
| FLOAT    | 单精度浮点数
| DOUBLE  | 双精度浮点数
| STRING   | 字符序列
| TIMESTAMP | 整数，浮点数或者字符串
| BINARY   | 字节数组

Hive中如果不同类型的两列值做运算会将转换为较大类型进行运算，和Java中类似。

**集合数据类型**

| 数据类型  | 描述  | 示例 |
| ------- | ------| ------|
| STRUCT  | 和C语言中类似,如果某个列的数据类型是STRUCT{first STRIG,last STRING}，那么第一个元素可以通过字段名.first来引用|struct('John','Doe') |
| MAP     | 键值对，可思议使用数组的方式来访问，如字段名['key'] | map('first','JOIN','last','Doe') |
| ARRAY   | 数组是一组具有相同类型的名称的变量的集合，可以通过数组索引的方式来访问 | Array('John','Doe') |

```
CREATE TABLE employees(
  name STRING,
  salary FLOAT,
  subordinates ARRAY<STRING>,
  debuctions MAP<STRING,FLOAT>,
  address STRUCT<street:STRING,city:STRING,state:STRING>)
  ROW FORMAT DELIMITED,
  FIELDS TERMINATED BY '',
  COLLECTION ITEMS TERMINATED BY '',
  MAP KEYS TERMINATED BY '',
  LINES TERMINATED BY '',
  STORED AS TEXTFILE;
```

传统的数据库都是在数据写的时候对数据，模式等进行检验，属于写时模式，
而Hive在数据装载时并不会进行校验，有时对表的创建，修改甚至损坏，在查询时才会进行校验，也就是读时模式。

### HiveQL：数据定义

Hive与其他SQL存在差异：Hive不支持行级插入操作，更新操作和删除操作，Hive也不支持事物。

Hive会为每一个数据库创建一个目录，数据库中的表将会以这个数据库目录的子目录的形式存储。、
数据库所在的目录位于属性"hive.metastore.warehouse.dir"所指定的顶层目录之后。

用户也可以通过下面命令来自行指定数据库的存储位置：
```
create database yd_db location '';
```
存储在hdfs文件系统中用`hdfs://`作为前缀，存储在本地文件系统中使用`file:///`作为前缀。

默认情况下，Hive是不允许删除一个包含有表的数据库的，用户要么先删除数据库中的表，然后再删除数据库；
要么在删除命令的最后加上关键字`CASCADE`，这样可以使hive自行先删除数据库中的表。

**修改数据库**

Hive中可以使用下面命令来修改描述该数据库的属性信息。但是数据库的其他元数据信息都是不可更改的，包括数据库名
和数据库所在的目录位置：
```
alter database db_yd set dbproperties("key"="value");
```

`tblproperties`关键字和`dbproperties`类似是按照键值对的格式为表添加额外的描述文档。

用户还可以拷贝一张的表的表模式，不拷贝数据：
```
create table if not exists mytb like emp;
```

**内部表/管理表**

当我删除一个管理表的时候，Hive也会删除这个表中的数据。

**外部表**

可以使用`external`关键字来创建外部表，删除表不会删除这份数据，不过描述表的元数据信息会被删除掉。

我们可以说使用下面命令来对一张已经存在的表进行表结构的复制（而不会复制数据）：
```
create external table if not exists myemp
like emp
location '';
```
注意：如果语句中省略external关键字，而原表是外部表的话，新表也是外部表，如果原表是内部表的话，新表也是内部表。
如果没有省略，但是原表是内部表的话，新表将会是外部表。

















