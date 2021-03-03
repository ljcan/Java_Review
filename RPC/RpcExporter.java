```
package rpc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RpcExporter {
    static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static void exporter(String hostname,int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(hostname, port));
        try{
            while (true){
                executor.execute(new ExporterTask(serverSocket.accept()));
            }
        }finally {
            if(serverSocket!=null){
                serverSocket.close();
            }
        }

    }

    public static class ExporterTask implements Runnable{
        Socket client = null;
        public ExporterTask(Socket client){
            this.client = client;
        }
        public void run() {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;

            try {
                inputStream = new ObjectInputStream(client.getInputStream());
                String interfaceName = inputStream.readUTF();
                Class<?> service = Class.forName(interfaceName);
                String methodName = inputStream.readUTF();
                Class<?>[] parmTypes = (Class<?>[]) inputStream.readObject();
                Object[] args = (Object[]) inputStream.readObject();
                Method method = service.getMethod(methodName, parmTypes);
                Object result = method.invoke(service.newInstance(), args);
                outputStream = new ObjectOutputStream(client.getOutputStream());
                outputStream.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(outputStream!=null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(client!=null){
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}

```
