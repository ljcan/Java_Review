--模块
module={}
module.constant="这是一个常量"
function module.func1()
  io.write("这是一个公有函数\n")
end

local function func2()
  io.write("这是一个私有函数\n")
end

function module.func3()
  return func2()
end
return module


