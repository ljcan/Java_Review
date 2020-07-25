
--使用select(n,...)用来访问变长参数的第n个参数；使用select('#',...)来访问变长参数的数量
function f(...)
  for i=1,select('#',...) do
	local arg=select(i,...);
        print("arg",arg)
	i=i+1
  end
end
f(1,2,3,4)
