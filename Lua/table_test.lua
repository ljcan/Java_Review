tab1={key1="val1",key2="val2","val3"}

for k,v in pairs(tab1) do
    print(k.."-"..v)
end

-- 下面代码等同于删除掉tab1表
--[[tab1=nil
for k,v in pairs(tab1) do
   print(k.."-"..v)
end]]--

