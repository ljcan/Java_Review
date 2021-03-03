```
package rpc.server.impl;

import rpc.server.EchoServer;

public class EchoServerImpl implements EchoServer {
    public String echo(String ping) {
        return ping!=null?ping+" --> I am ok":"I am ok";
    }
}

```
