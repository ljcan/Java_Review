```
package rpc.client;

import rpc.server.EchoServer;
import rpc.server.RpcExporter;
import rpc.server.impl.EchoServerImpl;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RpcTest {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    RpcExporter.exporter("localhost",8089);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        RpcImporter<EchoServer> rpcImporter = new RpcImporter<EchoServer>();
        EchoServer server = rpcImporter.importer(EchoServerImpl.class, new InetSocketAddress("localhost", 8089));
        System.out.println(server.echo("are you ok?"));

    }
}

```
