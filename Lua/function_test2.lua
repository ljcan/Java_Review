--匿名函数
function test(tab,func)
  for k,v in pairs(tab) do
     print(func(k,v))
  end
end

tab={"val1","val2"}
test(tab,function(key,val)
     return key..":"..val
end
)

