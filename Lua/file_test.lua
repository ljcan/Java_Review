-- 文件读写
file=io.open("module.lua","r")
--设置默认输入文件为module.lua
io.input(file)
--输出文件第一行
print(io.read())

