Java IO 方式有很多种，基于不同的 IO 抽象模型和交互方式，可以进行简单区分。


首先，传统的 java.io 包，它基于流模型实现，提供了我们最熟知的一些 IO 功能，比如 File 抽
象、输入输出流等。交互方式是同步、阻塞的方式，也就是说，在读取输入流或者写入输出流
时，在读、写动作完成之前，线程会一直阻塞在那里，它们之间的调用是可靠的线性顺序。
java.io 包的好处是代码比较简单、直观，缺点则是 IO 效率和扩展性存在局限性，容易成为应用
性能的瓶颈。
很多时候，人们也把 java.net 下面提供的部分网络 API，比如 Socket、ServerSocket、
HttpURLConnection 也归类到同步阻塞 IO 类库，因为网络通信同样是 IO 行为。


第二，在 Java 1.4 中引入了 NIO 框架（java.nio 包），提供了 Channel、Selector、Buffer 等
新的抽象，可以构建多路复用的、同步非阻塞 IO 程序，同时提供了更接近操作系统底层的高性
能数据操作方式。


第三，在 Java 7 中，NIO 有了进一步的改进，也就是 NIO 2，引入了异步非阻塞 IO 方式，也
有很多人叫它 AIO（Asynchronous IO）。异步 IO 操作基于事件和回调机制，可以简单理解
为，应用操作直接返回，而不会阻塞在那里，当后台处理完成，操作系统会通知相应线程进行后
续工作。


1. IO 不仅仅是对文件的操作，网络编程中，比如 Socket 通信，都是典型的 IO 操作目标。
输入流、输出流（InputStream/OutputStream）是用于读取或写入字节的，例如操作图片
文件。
2. 而 Reader/Writer 则是用于操作字符，增加了字符编解码等功能，适用于类似从文件中读取
或者写入文本信息。本质上计算机操作的都是字节，不管是网络通信还是文件读取，
Reader/Writer 相当于构建了应用逻辑和原始数据之间的桥梁。
3. BufferedOutputStream 等带缓冲区的实现，可以避免频繁的磁盘读写，进而提高 IO 处理
效率。这种设计利用了缓冲区，将批量数据进行一次操作，但在使用中千万别忘了 flush。
4. 很多 IO 工具类都实现了 Closeable 接口，因为需要进行资源的释放。
比如，打开 FileInputStream，它就会获取相应的文件描述符（FileDescriptor），需要利用
try-with-resources、 try-finally 等机制保证 FileInputStream 被明确关闭，进而相应文件
描述符也会失效，否则将导致资源无法被释放。

##### NIO多路复用实现原理
1. 首先，通过 Selector.open() 创建一个 Selector，作为类似调度员的角色。
2. 然后，创建一个 ServerSocketChannel，并且向 Selector 注册，通过指定SelectionKey.OP_ACCEPT，告诉调度员，它关注的是新的连接请求。
3. 注意，为什么我们要明确配置非阻塞模式呢？这是因为阻塞模式下，注册操作是不允许的，会抛出 IllegalBlockingModeException 异常。
4. Selector 阻塞在 select 操作，当有 Channel 发生接入请求，就会被唤醒。
5. 遍历selectKeys，发送客户端请求。
