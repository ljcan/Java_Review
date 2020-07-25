luasql=require "luasql.mysql"
env=luasql.mysql()
--连接数据库
conn=env:connect("test","root","123456","127.0.0.1",3306)
--设置数据库的编码格式
conn:execute"SET NAMES UTF8"
--执行数据库操作
cur=conn:execute("show tables")
print(cur)
