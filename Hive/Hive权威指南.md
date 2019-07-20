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
数据类型 | 长度
------- | -----
TINYINT | 1byte有符号整数
SMALINT |  2byte有符号整数
INT    |4byte有符号整数
BIGINT  | 8byte有符号整数
BOOLEAN  | 布尔类型
FLOAT    | 单精度浮点数
DOUBLE  | 双精度浮点数
STRING   | 字符序列
TIMESTAMP | 整数，浮点数或者字符串
BINARY   | 字节数组











