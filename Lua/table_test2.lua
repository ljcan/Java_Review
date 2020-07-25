a={}
a["key"]="value"
key=10
a[key]=10
a[key]=a[key]+10
for k,v in pairs(a) do
  print(k..":"..v)
end

