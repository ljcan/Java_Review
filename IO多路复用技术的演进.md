**同步与异步：** 同步与异步是指用户线程与内核的交互方式。同步是指线程等待或者轮询连接请求，等到数据到达处理完毕之后才会返回；异步是指线程发送请求不必等待
处理完毕才返回，而是直接返回，当数据到达处理完毕之后会通知用户线程或者调用用户注册的回调函数。

**阻塞与非阻塞：** 阻塞与非阻塞是指用户线程与内核IO的操作方式，阻塞是指一个IO请求必须完成请求才会返回给用户线程；非阻塞是指IO请求只需返回用户线程一个状态
信息，无需等待IO操作彻底完成。

#### 1.同步阻塞方式：

![同步阻塞方式](http://images.cnitblog.com/blog/405877/201411/142330286789443.png)

同步阻塞方式是指用户线程向系统发送IO请求时，必须等待数据到达处理完毕之后才会返回。

#### 2.同步非阻塞方式：

![同步非阻塞方式](http://images.cnitblog.com/blog/405877/201411/142332004602984.png)

同步非阻塞方式是指用户线程向系统发送IO请求，此时IO请求并不会阻塞而是直接返回用户一个状态信息，如果数据没有到达，则用户线程进行轮询访问，直到数据到达，IO
处理请求返回给用户线程，用户线程返回。

#### 3.IO多路复用技术：

IO多路复用的两个关键技术点：
1. 如果多个连接阻塞在一个对象上，那么系统进程不必去轮询，而是直接等待在该阻塞对象上。
2. 当有一个连接有新的数据可以请求的时候，那么操作系统会唤醒阻塞的进程，从而处理业务。

![IO多路复用](http://images.cnitblog.com/blog/405877/201411/142332187256396.png)
当用户请求到来的时候会阻塞在select函数上，向select函数注册信息，然后进程不断地调用select函数激活相应的请求进行操作。
IO多路复用虽然没有解决IO阻塞的问题，但是它可以利用一个线程来处理多个IO请求。

#### 4.Reactor模型
Reactor中文“反应堆”，也就是“事件反应”。Reactor模型基于IO多路复用与资源池来实现，Reactor模型由Reactor，资源池组件组成，利用IO多路复用处理
处理多个IO请求，然后将请求转发给相应的进程。

根据Reactor的数量可以有一个或者多个，根据资源池的数据可以有一个或者多个，这样组合起来就有：
- 单Reactor单进程/线程
- 单Reactor多进程/线程
- 多Reactor多进程/线程

##### 4.1 单Reactor单进程/线程模型
![单Reactor单进程](https://github.com/ljcan/Review/blob/master/Java/pictures/%E5%8D%95Reactor%E5%8D%95%E8%BF%9B%E7%A8%8B%E6%A8%A1%E5%9E%8B.png)

- Reactor对象通过select监控连接事件，收到请求之后dispatch进行转发。
- 如果是连接请求，则转发到Acceptor调用accept函数建立连接。
- 如果非连接请求，则转发到Handler分别调用read等API处理业务请求，最后通过send返回给客户端。

该模型有以下两个缺点：
1. 首先单个进程无法发挥多核cpu的性能。
2. 单个handler处理请求时会阻塞其他请求，称为性能的瓶颈。
**Redis使用单Reactor单进程模型**

##### 4.2 单Reactor多进程/线程模型
![单Reactor多进程/线程](https://github.com/ljcan/Review/blob/master/Java/pictures/%E5%8D%95Reactor%E5%A4%9A%E8%BF%9B%E7%A8%8B%E6%A8%A1%E5%9E%8B.png)

- Reactor对象通过select监控连接事件，收到请求之后dispatch进行转发。
- 如果是连接请求，则转发到Acceptor调用accept函数建立连接。
- 如果是非连接请求，则转发到Handler调用read函数读取请求。
- 将请求发送给子进程processor进行业务处理。
- 子进程处理完毕之后将结果返回给主进程的Handler。
- 主进程调用send函数返回给用户。

缺点：
1. 子进程处理完毕之后需要将结果发送给Reactor，存在线程安全问题，需要进行共享数据的保护。
2. 单个Reactor处理所有请求，高并发下是系统的瓶颈。

##### 4.3 duo Reactor多进程/线程模型
![多Reactor多进程/线程](https://github.com/ljcan/Review/blob/master/Java/pictures/%E5%A4%9AReactor%E5%A4%9A%E8%BF%9B%E7%A8%8B%E6%A8%A1%E5%9E%8B.png)

- mainReacotr进行连接事件的监听，将请求发送给Acceptor进行处理。
- Acceptor会将新的连接发送给subReactor进行处理。
- subReactor将接收的请求放入监听队列进行监听，并且创建相应的Handler来处理请求。
- Handler调用read等API完成业务处理，最终send给client。

**Nginx使用多Reactor多进程模型，Memcached和Netty使用多Reactor多线程模型。**




