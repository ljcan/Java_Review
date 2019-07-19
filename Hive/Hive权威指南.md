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







