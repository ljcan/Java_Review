--无状态迭代器
function f(x,y)
 if y<x then
  y=y+1
 return y,y*y
 end
end

for i,j in f,3,0 do
  print(i,j)
end

