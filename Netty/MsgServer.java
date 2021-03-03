```
package netty.msg;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class MsgServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup=null;
        EventLoopGroup workGroup=null;
        ServerBootstrap serverBootstrap=null;
        try{
            serverBootstrap = new ServerBootstrap();
            bossGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup(2);
            serverBootstrap.group(bossGroup, workGroup)
                    .localAddress("", 888)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                    .addLast(new MsgPackDecoder())
                                    .addLast(new LengthFieldPrepender(2))       //包头加长度字段进行解码，占两个字节
                                    .addLast(new MsgPackDecoder());
                        }
                    }).childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {          //释放线程资源
            if(workGroup!=null){
                workGroup.shutdownGracefully();
            }
            if(bossGroup!=null){
                bossGroup.shutdownGracefully();
            }
        }


    }
}

```
