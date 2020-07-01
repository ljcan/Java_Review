1. 查看某个进程运行时的环境变量：`cat /proc/$PID/environ`
2. 查看某一个进程的进程ID：`pgrep proc_name`
3. 替换命令 `tr '原文本' '新文本'`
4. `netstat` 命令用于显示各种网络相关信息，如网络连接，路由表，接口状态 (Interface Statistics)，masquerade 连接，多播成员 (Multicast Memberships) 等等。`netstat -p` 可以与其它开关一起使用，就可以添加 “PID/进程名称” 到 netstat 输出中，这样 debugging 的时候可以很方便的发现特定端口运行的程序。
5. `lsof（list open files）`是一个查看当前系统文件的工具
6. `pwdx pid`查看该进程号是哪个目录下的应用打开的。
