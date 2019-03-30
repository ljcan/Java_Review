#### Servlet生命周期

通常情况下，servlet第一次被访问的时候在内存中创建对象，在创建后立即调用init()方法进行初始化。对于每一次请求都掉用service(req,resp)方法处理请求，
此时会用Request对象封装请求信息，并用Response对象（最初是空的）代表响应消息，传入到service方法里供使用。当service方法处理完成后，
返回服务器服务器根据Response中的信息组织称响应消息返回给浏览器。响应结束后servlet并不销毁，一直驻留在内存中等待下一次请求。直到服务器关闭或
web应用被移除出虚拟主机，servlet对象销毁并在销毁前调用destroy()方法做一些善后的事情。

#### Servlet接口的继承结构

Servlet接口：定义了一个servlet应该具有的方法，所有的Servlet都应该直接或间接实现此接口
			
			|----GenericServlet：对Servlet接口的默认实现，通用Servlet，这是一个抽象类，其中的大部分方法都做了默认实现，
			只有service方法是一个抽象方法需要继承者自己实现。
						|
						|----HttpServlet:对HTTP协议进行了优化的Servlet，继承自GenericServlet类，
						并且实现了其中的service抽象方法，默认的实现中判断了请求的请求方式，并根据
						请求方式的不同分别调用不同的doXXX()方法。通常我们直接继承HttpServlet即可。

#### web.xml注册Servlet的注意事项
```
1. 利用<servlet><servlet-mapping>标签注册一个Servlet
				<servlet>
					<servlet-name>FirstServlet</servlet-name>
					<servlet-class>cn.itheima.FirstServlet</servlet-class>  
					注意：此处要的是一个Servlet的完整类名，不是包含.java或.class扩展的文件路径
				</servlet>
				<servlet-mapping>
					<servlet-name>FirstServlet</servlet-name>
					<url-pattern>/FirstServlet</url-pattern>
				</servlet-mapping>
			2. 一个<servlet>可以对应多个<servlet-mapping>
			3. 可以用*匹配符配置<serlvet-mapping>,但是要注意，必须是*.do或者/开头的以/*结束的路径。
				~由于匹配符的引入有可能一个虚拟路径会对应多个servlet-mapping，此时哪个最像找哪个servlet，并且*.do级别最低。
			4. 可以为<servlet>配置<load-on-startup>子标签，指定servlet随着服务器的启动而加载，其中配置的数值指定启动的顺序
				<servlet>
					<servlet-name>invoker</servlet-name>
					<servlet-class>
						org.apache.catalina.servlets.InvokerServlet
					</servlet-class>
					<load-on-startup>2</load-on-startup>
				</servlet>
			5. 缺省servlet：如果一个servlet的对外访问路径被设置为/，则该servlet就是一个缺省servlet，其他servlet不处理的
			请求都由它来处理,~在conf/web.xml中配置了缺省servlet，对静态资源的访问和错误页面的输出就是由这个缺省servlet来处理的。
		        如果我们自己写一个缺省servlet把web.xml中的缺省servlet覆盖的话，会导致静态web资源无法访问。所以不推荐配置。
```

#### Servlet线程安全问题
```
由于通常情况下，一个servlet在内存只有一个实例处理请求，当多个请求发送过来的时候就会有多个线程操作该servlet对象，此时可能导致线程安全问题。

解决方法
    (1)利用同步代码块解决问题。缺陷是，同一时间同步代码块只能处理一个请求，效率很低下，所以同步代码块中尽量只包含核心的导致线程安全问题的代码。 
    (2)为该servlet实现SingleThreadModel接口，此为一个标记接口，被标记的servlet将会在内存中保存一个servlet池，
       如果一个线程来了而池中没有servlet对象处理，则创建一个新的。如果池中有空闲的servlet则直接使用。
       这并不能真的解决线程安全问题。此接口已经被废弃。
    (3)两种解决方案都不够完美，所以尽量不要在servlet中出现成员变量。
```

#### ServletConfig
```
代表servlet配置的对象，可以在web.xml中<servlet>中配置
			<servlet>
			    <servlet-name>Demo5Servlet</servlet-name>
			    <servlet-class>cn.itheima.Demo5Servlet</servlet-class>
			    <init-param>
				<param-name>data1</param-name>
				<param-value>value1</param-value>
			    </init-param>
			</servlet>
			  然后在servlet中利用this.getServletConfig()获取ServletConfig对象，该对象提供了getInitParameter()和					  getInitParameterNames()方法，可以遍历出配置中的配置项。
			  不想在servlet中写死的内容可以配置到此处。
```

#### ServletContext
```
1.代表当前web应用的对象。

2.作为域对象使用，在不同servlet之间传递数据,作用范围是整个web应用生命周期：当web应用被加载进容器时创建代表整个web应用的ServletContext对象。
当服务器关闭或web应用被移除出容器时，ServletContext对象跟着销毁。
~域：一个域就理解为一个框，这里面可以放置数据，一个域既然称作域，他就有一个可以被看见的范围，这个范围内都可以对这个域中的数据进行操作，
那这样的对象就叫做域对象。

3.在web.xml可以配置整个web应用的初始化参数，利用ServletContext去获得
			<context-param>
				<param-name>param1</param-name>
				<param-value>pvalue1</param-value>
			</context-param>
			this.getServletContext().getInitParameter("param1")
			this.getServletContext().getInitParameterNames()
		
4.在不同servlet之间进行转发
	this.getServletContext().getRequestDispatcher("/servlet/Demo10Servlet").forward(request, response);
	方法执行结束，service就会返回到服务器，再有服务器去调用目标servlet，其中request会重新创建，并将之前的request的数据拷贝进去。
			
5.读取资源文件
	5.1由于相对路径默认相对的是java虚拟机启动的目录，所以我们直接写相对路径将会是相对于tomcat/bin目录，所以是拿不到资源的。
	   如果写成绝对路径，当项目发布到其他环境时，绝对路径就错了。
	5.2为了解决这个问题ServletContext提供了this.getServletContext().getRealPath("/1.properties")，给一个资源的虚拟路径，
	   将会返回该资源在当前环境下的真实路径。
	   this.getServletContext().getResourceAsStream("/1.properties")，给一个资源的虚拟路径返回到该资源真实路径的流。
	5.3当在非servlet下获取资源文件时，就没有ServletContext对象用了，此时只能用类加载器
	   classLoader.getResourceAsStream("../../1.properties")，此方法利用类加载器直接将资源加载到内存中，有更新延迟的问题，
	   以及如果文件太大，占用内存过大。
	   classLoader.getResource("../1.properties").getPath()，直接返回资源的真实路径，没有更新延迟的问题。
```



















