`String str = new String("java");`

上面这段代码实际上创建了两个字符串对象，一个是“java”这个直接量对应的字符串对象，另一个是由new String()构造器返回的字符串对象。

在java中创建对象的常见4种方式：
- 通过new调用构造器创建java对象。
- 通过Class对象的newInstance()方法调用构造器来创建java对象。
- 通过java的反序列化机制从IO流中恢复java对象。
- 通过java对象提供的clone()方法复制一个新的java对象（浅拷贝）。

**构造器创建对象吗？**

实际上构造器并不会创建java对象，构造器只是负责执行初始化，在构造器之前，java对象所需要的内存空间，应该说是由new关键字申请出来的。

有时创建对象时并不需要构造器，使用以下两种方式来创建：
- 使用反序列化的方式来恢复java对象。
- 使用clone方法复制java对象。

**native方法**

native方法像抽象方法一样只有方法签名，没有方法体。

native方法需要借助C语言来完成，实现步骤如下：
1. 用javah编译生成的class文件，将产生一个.h文件。
2. 写一个.cpp文件实现native方法，其中需要包含第一步产生的.h文件（.h文件中又包含了JDK带的jni.h文件）。
3. 将第二步的.cpp文件编译成动态链接库文件。
4. 在Java中用System的loadLibrary()方法或Runtime的loadLibrary()方法加载第三步产生的动态链接文件，就可以在java程序中调用这个native方法了。

java调用C++代码：![java和C++混编](https://www.cnblogs.com/moon1992/p/5260226.html)

**java 7增强的try语句关闭资源**

它允许在try关键字后紧跟一对圆括号可以声明，初始化一个或多个资源，此处的资源指的是那些必须在程序结束时显式关闭的资源（比如数据库连接，网络连接等），
try语句会在该语句结束时自动关闭这些资源。

需要注意的是，为了保证try语句可以正常关闭这些资源，这些资源实现类必须实现AutoCloseable或Closeable接口，实现这两个接口就必须实现close()方法。

**finally块**

finally块代表总是会被执行的代码块，但有一种情况例外，在try语句块中有System.exit(0);语句块，此时程序不会执行finally块的代码。

当System.exit(0)被调用时，虚拟机退出前要执行两项清理工作。
- 执行系统中注册的所有关闭钩子。
- 如果程序调用了System.runFinalizerOnExit(true);，那么要JVM会对所有还未结束的对象调用Finalizer。

当try和finally语句块中都有return语句时，当程序执行到try语句块中的时候不会先返回，会接着执行finally语句块中的finally，然后执行finally语句块中的
return语句返回。
