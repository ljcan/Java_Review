```
package rabbitmq.demo;

import java.io.*;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private long msgSeq;
    private String msgBody;
    private long deliveryTag;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getMsgSeq() {
        return msgSeq;
    }

    public void setMsgSeq(long msgSeq) {
        this.msgSeq = msgSeq;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public long getDeliveryTag() {
        return deliveryTag;
    }

    public void setDeliveryTag(long deliveryTag) {
        this.deliveryTag = deliveryTag;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msgSeq=" + msgSeq +
                ", msgBody='" + msgBody + '\'' +
                ", deliveryTag='" + deliveryTag + '\'' +
                '}';
    }

    public static byte[] getByeteFromBytes(Object object) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(object);
        oo.close();
        bo.close();
        return bo.toByteArray();
    }

    public static Object getObjectFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        oi.close();
        bi.close();
        return oi.readObject();
    }
}

```
