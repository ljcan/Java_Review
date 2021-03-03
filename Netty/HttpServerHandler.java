```
package netty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.CharSetUtils;
//入站ChannelInBoundHandler类型的处理器，负责接收HTTP请求数据，并将请求处理结果写回客户端
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    protected void channelRead0(ChannelHandlerContext ctx,
                                FullHttpRequest fullHttpRequest) throws Exception {
        String content = String.format("Receive http request , uri :%s,method :%s,content: %s%n",fullHttpRequest.uri(),fullHttpRequest.method(),fullHttpRequest.content().toString(CharsetUtil.UTF_8));
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content.getBytes()));
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }
}

```
