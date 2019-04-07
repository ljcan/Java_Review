```
2.2、TOMCAT服务器的安装与配置
		2.2.1.常见服务器：WebLogic（BEA）、webSphere（IBM）、Tomcat（Apache）
		2.2.2.Tomcat 的下载与安装
				下载地址：http://tomcat.apache.org/
				安装目录不能包含中文和空格
				JAVA_HOME环境变量指定Tomcat运行时所要用的jdk所在的位置，注意，配到目录就行了，不用指定到bin
				端口占用问题：netstat -ano命令查看端口占用信息
				Catalina_Home环境变量：startup.bat启动哪个tomcat由此环境变量指定，如果不配置则启动当前tomcat，推荐不要配置此环境变量
		2.2.3.Tomcat的目录结构
			bin--存放tomcat启动关闭所用的批处理文件
			conf--tomcat的配置文件，最终要的是server.xml
				*实验:修改servlet.xml,更改tomcat运行所在的端口号，从8080改为80
			lib--tomcat运行所需jar包
			logs--tomcat运行时产生的日志文件
			temp--tomcat运行时使用的临时目录，不需要我们关注
			webapps--web应用所应存放的目录
			work--tomcat工作目录，后面学jsp用到
		2.2.4.虚拟主机（一个真实主机可以运行多个网站，对于浏览器来说访问这些网站感觉起来就像这些网站都运行在自己的独立主机中一样，所以，我们可以说这里的每一个网站都运行在一个虚拟主机上，一个网站就是一个虚拟主机）
			4.1配置虚拟主机
				在server.xml中<Engine>标签下配置<Host>,其中name属性指定虚拟主机名，appBase指定虚拟主机所在的目录
				只在servlet.xml中配置Hosts，还不能是其他人通过虚拟主机名访问网站，还需要在DNS服务器上注册一把，我们可以使用hosts文件模拟这个过程
				默认虚拟主机：在配置多个虚拟主机的情况下，如果浏览器使用ip地址直接访问网站时，该使用哪个虚拟主机响应呢？可以在<Engine>标签上设置defaultHost来指定
		2.2.5.web应用（web资源不能直接交给虚拟主机，需要按照功能组织用目录成一个web应用再交给虚拟主机管理）
				5.1web应用的目录结构
					web应用目录
						|
							-html、css、js、jsp
						|
							-WEB-INF
								|
									-classes
								|
									-lib
								|
									-web.xml
					5.2web.xml文件的作用：
						某个web资源配置为web应用首页
						将servlet程序映射到某个url地址上
						为web应用配置监听器
						为web应用配置过滤器
						但凡涉及到对web资源进行配置，都需要通过web.xml文件
							*实验：配置一个web应用的主页
					5.3web应用的虚拟目录映射
						（1）在server.xml的<Host>标签下配置<Context path="虚拟路径" docBase="真实路径">如果path=""则这个web应用就被配置为了这个虚拟主机的默认web应用
						（2）在tomcat/conf/引擎名/虚拟主机名 之下建立一个.xml文件，其中文件名用来指定虚拟路径，如果是多级的用#代替/表示，文件中配置<Context docBase="真实目录">，如果文件名起为ROOT.xml则此web应用为默认web应用
						（3）直接将web应用放置到虚拟主机对应的目录下，如果目录名起为ROOT则此web应用为默认web应用
						~如果三处都配置默认web应用则server.xml > config/.../xx.xml > webapps
					5.4杂项
						(1)打war包：方式一：jar -cvf news.war * 方式二：直接用压缩工具压缩为zip包，该后缀为.war
						(2)通用context和通用web.xml，所有的<Context>都继承子conf/context.xml,所有的web.xml都继承自conf/web.xml
						(3)reloadable让tomcat自动加载更新后的web应用，当java程序修改后不用重启，服务器自动从新加载，开发时设为true方便开发，发布时设为false，提高性能
						(4)Tomcat管理平台，可以在conf/tomcat-users.xml下配置用户名密码及权限
						
```
