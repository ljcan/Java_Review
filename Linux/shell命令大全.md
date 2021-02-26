选出当前目录下的所有二进制文件：

`ll -rth |awk {'print $9'}|xargs file|grep bash|awk {'print $1'}|tr -d ':'`

访问最近七天被访问过的文件：

`find . -atime 7 -type f -print`

访问七天内被访问过的文件：

```
find . -atime -7 -type f -print
```

查询七天前被访问过的文件：

```
find . -atime +7 type f -print
```

查询大小超过2k的文件：

```
find . -type f -size +2k
```

按照权限查找：

`find . -type f -perm 644 -print //找具有可执行权限的所有文件`

查询用户所属的文件：

`find . -type f -user root -print`

搜索当前目前下的.txt文件，并且将其复制到上层目录：

`find . -type f -name "*.txt" -exec cp {} ../ \;`

统计文本的wordcount

`cat test.file |awk -F':'  '{print $2}'|awk '{print $1}'|sort|uniq -c`

`cat test.txt |awk -F' ' '{print$1"\n"$2}'|sort|uniq -c`

`cat test.txt |xargs -n 1|sort|uniq -c`

`cat test.txt |awk -F' ' '{print$1"\n"$2}'| xargs -I {} ./tt.sh {}`

已匹配的字符串通过标记&来引用.

```
echo this is en example | sed -r 's/\w+/[&]/g'
$>[this]  [is] [en] [example]
```

sed通常用单引号来引用；也可使用双引号，使用双引号后，双引号会对表达式求值

````
p=pattern
r=replace
echo this is a pattern|sed "s/$p/$r/g"
````

字符串插入字符：将文本中每行内容（ABCDEF） 转换为 ABC/DEF:

`echo ABCDEF|sed 's/^.\{3\}/&\//g'`

`echo -e "line1\nline2" |awk 'BEGIN{print "start"} {print} END{print "End"}'`

`echo -e "line1 f2 f3\n line2 \n line 3" | awk '{print NR":"$0"-"$1"-"$2}'`

统计行号：

```Shell
awk 'END {print NR}' test.txt
wc -l test.txt
```

awk实现head和tail命令：

```
head：awk 'NR<=10{print}' filename
tail：awk '{buffer[NR%10]=$0;}END{for(i=0;i<11;i++){
  print buffer[NR%10]}}' filename
  
  awk '{buffer[NR]=$0;num="wc -l test.txt"}END{for(int i=0;i<num;i++){\
    for word in buffer[NR]\
     do \
     echo $word;\
     done\
  }}'\
```

打印每一行：

```
while read line;do echo $line;done <test.txt
```

```
cat test.txt |(while read line; do (for word in $line; do echo $word;done) done)
```

对比文件每行的内容

```
paste -d "," word.txt word1.txt |awk -F',' '{if($1 !~ $2) {print$1,"\t",$2}}'

paste -d "," nginx.conf1 nginx.conf2 |awk -F',' '{if($1 !~ $2) {print$1,"\t",$2}}'

```

查看cpu核数：

`cat /proc/cpuinfo |grep processor|wc -l`

统计当前文件下不同用户的普通文件总数是多少？

`ls -l |awk 'NR!=1&&!/^d/{sum[$3]++} END{for(i in sum) printf "%-6s %-5s %-3s \n",i," ",sum[i]}'`

auditd工具：

`service auditd status`检查服务状态。

`service auditd restart`重启服务。

`auditctl -a exit,always -F arch=b64 -S kill`监听所有的kill信号。

将文件中所有文本单行输出：

`cat file.txt| xargs`

执行列数输出：

`cat test.txt |xargs -n 3`


```
paste -d "," word.txt word1.txt |awk -F',' '{if($1 !~ $2) {print$1,"\t",$2}}'

paste -d "," nginx.conf1 nginx.conf2 |awk -F',' '{if($1 !~ $2) {print$1,"\t",$2}}'

```

**trap命令**用于指定在接收到信号后将要采取的动作，常见的用途是在脚本程序被中断时完成清理工作。当shell接收到sigspec指定的信号时，arg参数（命令）将会被读取，并被执行。例如：

```
trap "exit 1" HUP INT PIPE QUIT TERM
```

表示当shell收到HUP INT PIPE QUIT TERM这几个命令时，当前执行的程序会读取参数“exit 1”，并将它作为命令执行。

语法：

```
trap [-lp] [[arg] sigspec ...]
```

**kill**命令可以带信号号码选项，也可以不带。如果没有信号号码，kill命令就会发出终止信号(15)，这个信号可以被进程捕获，使得进程在退出之前可以清理并释放资源。也可以用kill向进程发送特定的信号。

要撤销所有的后台作业，可以输入kill 0。

-查看当前进程的namespace：$$ 指向当前进程的ID号

`ls -l /proc/$$/ns`

Awk过滤history输出，找到最常用的命令：

```
history | awk '{a[$4]++}END{for(i in a){print a[i] " " i}}' | sort -rn | head
```

过滤文件中的重复行：

```
awk '!x[$0]++' <file>
```

计算当前机器内存使用率最高的top10进程：

```shell
ps -aux|grep -v "USER"|sort -n -r -k 4|awk '{print$11}'|head -n 10
```





