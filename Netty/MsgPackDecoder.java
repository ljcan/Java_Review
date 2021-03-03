```
package netty.msg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * 反序列化
 */
public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int len = byteBuf.readableBytes();
        byte[] buffer = new byte[len];
        byteBuf.getBytes(byteBuf.readerIndex(),buffer,0,len);
        MessagePack messagePack = new MessagePack();    //使用msgPack序列化框架将其序列化为object对象，然后将解码后的对象直接放入列表中
        list.add(messagePack.read(buffer));
    }
}

```
