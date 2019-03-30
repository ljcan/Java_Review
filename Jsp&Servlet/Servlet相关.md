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

#### Response/Request
```
（1）、Response
		1.Resonse的继承结构：
			ServletResponse--HttpServletResponse
		2.Response代表响应，于是响应消息中的 状态码、响应头、实体内容都可以由它进行操作,由此引伸出如下实验：
		3.利用Response输出数据到客户端
			response.getOutputStream（）.write("中文".getBytes())输出数据，这是一个字节流，是什么字节输出什么字节，
			而浏览器默认用平台字节码打开服务器发送的数据，如果服务器端使用了非平台码去输出字符的字节数据就需要明确的指定
			浏览器编码时所用的码表，以防止乱码问题。response.addHeader("Content-type","text/html;charset=gb2312")
			response.getWriter().write(“中文”);输出数据，这是一个字符流，response会将此字符进行转码操作后输出到浏览器，
			这个过程默认使用ISO8859-1码表，而ISO8859-1中没有中文，于是转码过程中用?代替了中文，导致乱码问题。
			可以指定response在转码过程中使用的目标码表，防止乱码。response.setCharcterEncoding("gb2312");
			其实response还提供了setContentType("text/html;charset=gb2312")方法，此方法会设置content-type响应头，
			通知浏览器打开的码表，同时设置response的转码用码表，从而一行代码解决乱码。
		4.利用Response 设置 content-disposition头实现文件下载
			设置响应头content-disposition为“attachment;filename=xxx.xxx”
			利用流将文件读取进来，再利用Response获取响应流输出
			如果文件名为中，一定要进行URL编码，编码所用的码表一定要是utf-8
		5.refresh头控制定时刷新
			设置响应头Refresh为一个数值，指定多少秒后刷新当前页面
			设置响应头Refresh为 3;url=/Day05/index.jsp,指定多少秒后刷新到哪个页面
			可以用来实现注册后“注册成功，3秒后跳转到主页”的功能
			在HTML可以利用<meta http-equiv= "" content="">标签模拟响应头的功能。
		6.利用response设置expires、Cache-Control、Pragma实现浏览器是否缓存资源，这三个头都可以实现，但是由于历史原因，
		  不同浏览器实现不同，所以一般配合这三个头使用
			6.1控制浏览器不要缓存（验证码图片不缓存）设置expires为0或-1设置Cache-Control为no-cache、Pragma为no-cache
			6.2控制浏览器缓存资源。即使不明确指定浏览器也会缓存资源，这种缓存没有截至日期。当在地址栏重新输入地址时会用缓存，
			   但是当刷新或重新开浏览器访问时会重新获得资源。
			   如果明确指定缓存时间，浏览器缓存是，会有一个截至日期，在截至日期到期之前，当在地址栏重新输入地址或重新开
			   浏览器访问时都会用缓存，而当刷新时会重新获得资源。
		7.Response实现请求重定向
			7.1古老方法：response.setStatus(302);response.addHeader("Location","URL");
			7.2快捷方式：response.sendRedirect("URL");
		8.输出验证码图片

		9.Response注意的内容：
			9.1对于一次请求，Response的getOutputStream方法和getWriter方法是互斥，只能调用其一，
			   特别注意forward后也不要违反这一规则。
			9.2利用Response输出数据的时候，并不是直接将数据写给浏览器，而是写到了Response的缓冲区中，
			   等到整个service方法返回后，由服务器拿出response中的信息组成HTTP响应消息返回给浏览器。
			9.3service方法返回后，服务器会自己检查Response获取的OutputStream或者Writer是否关闭，如果没有关闭，
			   服务器自动帮你关闭，一般情况下不要自己关闭这两个流。
			   
（2）、Request：
	Request代表请求对象，其中封装了对请求中具有请求行、请求头、实体内容的操作的方法
		1.获取客户机信息
			getRequestURL方法返回客户端发出请求完整URL
			getRequestURI方法返回请求行中的资源名部分,在权限控制中常用
			getQueryString 方法返回请求行中的参数部分
			getRemoteAddr方法返回发出请求的客户机的IP地址
			getMethod得到客户机请求方式
			getContextPath 获得当前web应用虚拟目录名称，特别重要！！！，工程中所有的路径请不要写死，其中的web应用名要以此方法去获得。

		2.获取请求头信息
			getHeader(name)方法 --- String ，获取指定名称的请求头的值
			getHeaders(String name)方法 --- Enumeration<String> ，获取指定名称的请求头的值的集合，因为可能出现多个重名的请求头
			getHeaderNames方法 --- Enumeration<String> ，获取所有请求头名称组成的集合
			getIntHeader(name)方法  --- int ，获取int类型的请求头的值
			getDateHeader(name)方法 --- long(日期对应毫秒) ，获取一个日期型的请求头的值，返回的是一个long值，
			从1970年1月1日0时开始的毫秒值

		*实验：通过referer信息防盗链
			String ref = request.getHeader("Referer");
			if (ref == null || ref == "" || !ref.startsWith("http://localhost")) {
			response.sendRedirect(request.getContextPath() + "/homePage.html");
			} else {
			this.getServletContext().getRequestDispatcher("/WEB-INF/fengjie.html").forward(request, response);
			}
		3.获取请求参数
			getParameter(name) --- String 通过name获得值
			getParameterValues（name）  --- String[ ] 通过name获得多值 checkbox
			getParameterNames  --- Enumeration<String> 获得所有请求参数名称组成的枚举
			getParameterMap  --- Map<String,String[ ]> 获取所有请求参数的组成的Map集合，注意，其中的键为String，值为String[]

			获取请求参数时乱码问题：
			浏览器发送的请求参数使用什么编码呢？当初浏览器打开网页时使用什么编码，发送就用什么编码。
			服务器端获取到发过来的请求参数默认使用ISO8859-1进行解码操作，中文一定有乱码问题
			对于Post方式提交的数据，可以设置request.setCharacterEncoding("gb2312");来明确指定获取请求参数时使用编码。
			但是此种方式只对Post方式提交有效。
			对于Get方式提交的数据，就只能手动解决乱码：String newName = new String(name.getBytes("ISO8859-1"),"gb2312");
			此种方法对Post方式同样有效。
			在tomcat的server.xml中可以配置http连接器的URIEncoding可以指定服务器在获取请求参数时默认使用的编码，
			从而一劳永逸的决绝获取请求参数时的乱码问题。
			也可以指定useBodyEncodingForURI参数，令request.setCharacterEncoding也对GET方式的请求起作用，
			但是这俩属性都不推荐使用，因为发布环境往往不允许修改此属性。


		4.利用请求域传递对象
			生命周期：在service方法调用之前由服务器创建，传入service方法。整个请求结束，request生命结束。
			作用范围：整个请求链。
			作用：在整个请求链中共享数据，最常用的：在Servlet中处理好的数据要交给Jsp显示，此时参数就可以放置在Request域中带过去。

		5.request实现请求转发
			ServletContext可以实现请求转发，request也可以。
			在forward之前输入到response缓冲区中的数据，如果已经被发送到了客户端，forward将失败，抛出异常
			在forward之前输入到response缓冲区中的数据，但是还没有发送到客户端，forward可以执行，但是缓冲区将被清空，之前的数据丢失。				注意丢失的只是请求体中的内容，头内容仍然有效。
			在一个Servlet中进行多次forward也是不行的，因为第一次forward结束，response已经被提交了，没有机会再forward了
			总之，一条原则,一次请求只能有一次响应，响应提交走后，就再没有机会输出数据给浏览器了。

		6.RequestDispatcher进行include操作
			forward没有办法将多个servlet的输出组成一个输出，因此RequestDispatcher提供了include方法，
			可以将多个Servlet的输出组成一个输出返回个浏览器
			request.getRequestDispatcher("/servlet/Demo17Servlet").include(request, response);
			response.getWriter().write("from Demo16");
			request.getRequestDispatcher("/servlet/Demo18Servlet").include(request, response);
			常用在页面的固定部分单独写入一个文件，在多个页面中include进来简化代码量。
```

#### URL编码
```
1.由于HTTP协议规定URL路径中只能存在ASCII码中的字符，所以如果URL中存在中文或特殊字符需要进行URL编码。
		2.编码原理：
			将空格转换为加号（+） 
			对0-9,a-z,A-Z之间的字符保持不变 
			对于所有其他的字符，用这个字符的当前字符集编码在内存中的十六进制格式表示，并在每个字节前加上一个百分号（%）。
			如字符“+”用%2B表示，字符“=”用%3D表示，字符“&”用%26表示，每个中文字符在内存中占两个字节，字符“中”用%D6%D0表示，
			字符“国”用%B9%FA表示调对于空格也可以直接使用其十六进制编码方式，即用%20表示，而不是将它转换成加号（+） 
			说明：
			如果确信URL串的特殊字符没有引起使用上的岐义或冲突你也可以对这些字符不进行编码，而是直接传递给服务器。
			例如，http://www.it315.org/dealregister.html?name=中国&password=123 
			如果URL串中的特殊字符可能会产生岐义或冲突，则必须对这些特殊字符进行URL编码。
			例如，服务器会将不编码的“中+国”当作“中国”处理。还例如，当name参数值为“中&国”时，如果不对其中的“&”编码，
			URL字符串将有如下形式：http://www.it315.org/dealregister.html?name=中&国&password=123，
			应编码为：http://www.it315.org/dealregister.html?name=中%26国&password=123 
			http://www.it315.org/example/index.html#section2可改写成http://www.it315.org/example%2Findex.html%23section2 
		3.在java中进行URL编码和解码
			URLencoder.encode("xxxx","utf-8");
			URLDecoder.decode(str,"utf-8");
```

#### 请求和重定向的区别
```
		1.区别
			1) RequestDispatcher.forward方法只能将请求转发给同一个WEB应用中的组件；而HttpServletResponse.sendRedirect 
			方法还可以重定向到同一个站点上的其他应用程序中的资源，甚至是使用绝对URL重定向到其他站点的资源。 
			
			2) 如果传递给HttpServletResponse.sendRedirect 方法的相对URL以“/”开头，它是相对于服务器的根目录；
			如果创建RequestDispatcher对象时指定的相对URL以“/”开头，它是相对于当前WEB应用程序的根目录。 
			
			3) 调用HttpServletResponse.sendRedirect方法重定向的访问过程结束后，浏览器地址栏中显示的URL会发生改变，
			由初始的URL地址变成重定向的目标URL；调用RequestDispatcher.forward 方法的请求转发过程结束后，
			浏览器地址栏保持初始的URL地址不变。
			
			4) HttpServletResponse.sendRedirect方法对浏览器的请求直接作出响应，响应的结果就是告诉浏览器去重新发出对
			另外一个URL的访问请求；RequestDispatcher.forward方法在服务器端内部将请求转发给另外一个资源，
			浏览器只知道发出了请求并得到了响应结果，并不知道在服务器程序内部发生了转发行为。 
			
			5) RequestDispatcher.forward方法的调用者与被调用者之间共享相同的request对象和response对象，
			它们属于同一个访问请求和响应过程；而HttpServletResponse.sendRedirect方法调用者与被调用者使用
			各自的request对象和response对象，它们属于两个独立的访问请求和响应过程。 
		
		2.应用场景（参照图想）
			通常情况下都用请求转发，减少服务器压力
			当需要更新地址栏时用请求重定向，如注册成功后跳转到主页。
			当需要刷新更新操作时用请求重定向，如购物车付款的操作。
```

















