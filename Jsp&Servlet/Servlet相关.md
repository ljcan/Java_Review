#### Servlet生命周期

通常情况下，servlet第一次被访问的时候在内存中创建对象，在创建后立即调用init()方法进行初始化。对于每一次请求都掉用service(req,resp)方法处理请求，
此时会用Request对象封装请求信息，并用Response对象（最初是空的）代表响应消息，传入到service方法里供使用。当service方法处理完成后，
返回服务器服务器根据Response中的信息组织称响应消息返回给浏览器。响应结束后servlet并不销毁，一直驻留在内存中等待下一次请求。直到服务器关闭或
web应用被移除出虚拟主机，servlet对象销毁并在销毁前调用destroy()方法做一些善后的事情。

#### Servlet接口的继承结构

Servlet接口：定义了一个servlet应该具有的方法，所有的Servlet都应该直接或间接实现此接口
			|
			|----GenericServlet：对Servlet接口的默认实现，通用Servlet，这是一个抽象类，其中的大部分方法都做了默认实现，只有service方法是一个抽象方法需要继承者自己实现。
						|
						|----HttpServlet:对HTTP协议进行了优化的Servlet，继承自GenericServlet类，并且实现了其中的service抽象方法，默认的实现中判断了请求的请求方式，并根据请求方式的不同分别调用不同的doXXX()方法。通常我们直接继承HttpServlet即可。
