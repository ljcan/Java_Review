```
package netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class HttpClient {
//    boolean t = true;
    public void connect(String host,int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE,true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new HttpResponseDecoder());
                    socketChannel.pipeline().addLast(new HttpRequestEncoder());
                    socketChannel.pipeline().addLast(new HttpClientHandler());
                }
            });
            ChannelFuture future = b.connect(host, port).sync();

//            future.addListener()   //或者可以添加监听器，来判断异步连接返回的结果
//            future.wait();    //因为connect的连接是异步的，tcp的连接结果不得而知，因此可以在这里wait，直到被完成后notify();

            URI uri = new URI("http://127.0.0.1:8089");
            String content = "hello world";
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8)));
            request.headers().set(HttpHeaderNames.HOST,host);
            request.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
            //在返回的channelFuture方法中将注册结果发送给listener，通过operationComplete方法来判断发送的结果，如果发送失败
            //将之前得消息对象添加到重发队列中
            ChannelFuture channelFuture = future.channel().write(request);

            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    boolean success = future.isSuccess();
                    if(!success){
                        //重新发送
//                        future.get();
                    }
                }
            });


            future.channel().flush();
            //channel的closeFuture方法很方便检测链路状态，一旦链路关闭，xi相关事件即被触发，可以重新发起连接操作。
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {     //可以在finally中在连接超时的时候等待客户端释放资源句柄，并且重新发起连接操作
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        HttpClient client = new HttpClient();
        client.connect("127.0.0.1",8089);
    }
}

```
