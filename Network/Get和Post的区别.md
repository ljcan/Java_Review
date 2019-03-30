##### Get和POST的区别：
1. GET在浏览器回退时是无害的，而POST会再次提交请求。
2. GET产生的URL地址可以被Bookmark，而POST不可以。
3. GET请求会被浏览器主动cache，而POST不会，除非手动设置。
4. GET请求只能进行url编码，而POST支持多种编码方式。
5. GET请求参数会被完整保留在浏览器历史记录里，而POST中的参数不会被保留。
6. GET请求在URL中传送的参数是有长度限制的，而POST么有。
7. 对参数的数据类型，GET只接受ASCII字符，而POST没有限制。
8. GET比POST更不安全，因为参数直接暴露在URL上，所以不能用来传递敏感信息。
9. GET参数通过URL传递，POST放在Request body中。
10. **GET产生一个TCP数据包；POST产生两个TCP数据包。对于GET方式的请求，浏览器会把http header和data一并发送出去，服务器响应200（返回数据）；
而对于POST，浏览器先发送header，服务器响应100 continue，浏览器再发送data，服务器响应200 ok（返回数据）。**


HTTP请求：
```
请求行
			请求方法GET/POST
			请求资源URL
			HTTP协议版本号	
请求头
			Accept: text/html,image/*    客户端可以接受的数据类型
			Accept-Charset: ISO-8859-1	客户端接受数据需要使用的字符集编码
			Accept-Encoding: gzip,compress 客户端可以接受的数据压缩格式
			Accept-Language: en-us,zh-cn  可接受的语言环境
			Host: www.it315.org:80 想要访问的虚拟主机名
			If-Modified-Since: Tue, 11 Jul 2000 18:23:51 GMT 这是和缓存相关的一个头，带着缓存资源的最后获取时间
			Referer: http://www.it315.org/index.jsp 这个头表示当前的请求来自哪个链接，这个头和防盗链的功能相关
			User-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0) 客户端的一些基本信息
			Cookie 
			Connection: close/Keep-Alive 指定是否继续保持连接
			Date: Tue, 11 Jul 2000 18:23:51 GMT 当前时间
请求正文
			一般都是空的，可选的		
```

HTTP响应：
```
状态行
			HTTP/1.1 200 OK
			格式： HTTP版本号　状态码　原因叙述<CRLF>
			状态码：
				200：请求处理成功
				302：请求重定向
				304、307：服务器通知浏览器使用缓存
				404：资源未找到
				500：服务器端错误

若干响应头
				Location: http://www.it315.org/index.jsp  配合302实现请求重定向
				Server:apache tomcat 服务器的基本信息
				Content-Encoding: gzip 服务器发送数据时使用的压缩格式
				Content-Length: 80 发送数据的大小
				Content-Language: zh-cn 发送的数据使用的语言环境
				Content-Type: text/html; charset=GB2312 当前所发送的数据的基本信息，（数据的类型，所使用的编码）
				Last-Modified: Tue, 11 Jul 2000 18:23:51 GMT 缓存相关的头
				Refresh: 1;url=http://www.it315.org 通知浏览器进行定时刷新，此值可以是一个数字指定多长时间以后刷新当前页面，这个数字之后也可以接一个分号后跟一个URL地址指定多长时间后刷新到哪个URL
				Content-Disposition: attachment;filename=aaa.zip 与下载相关的头
				Transfer-Encoding: chunked 传输类型，如果是此值是一个chunked说明当前的数据是一块一块传输的
				Set-Cookie:SS=Q0=5Lb_nQ; path=/search 和cookie相关的头，后面课程单讲
				ETag: W/"83794-1208174400000" 和缓存机制相关的头
				Expires: -1 指定资源缓存的时间，如果取值为0或-1浏览就不缓存资源
				Cache-Control: no-cache  缓存相关的头，如果为no-cache则通知浏览器不缓存
				Pragma: no-cache   缓存相关的头，如果为no-cache则不缓存
				以上三个头都是用来控制缓存的，是因为历史原因造成的，不同的浏览器认识不同的头，我们通常三个一起使用保证通用性。
				Connection: close/Keep-Alive   是否保持连接
				Date: Tue, 11 Jul 2000 18:23:51 GMT 当前时间
		
实体内容
```
