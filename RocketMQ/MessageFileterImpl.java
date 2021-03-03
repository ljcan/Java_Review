```
package filter;

import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.filter.FilterContext;
import org.apache.rocketmq.common.filter.MessageFilter;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.Callable;

/**
 * filter server方式过滤消息
 */
public class MessageFileterImpl implements MessageFilter {
    @Override
    public boolean match(MessageExt messageExt, FilterContext filterContext) {
        String property = messageExt.getUserProperty("");
        if(property!=null){
            int id = Integer.parseInt(property);
            if((id%3)==0&&(id>10)){
                return true;
            }
        }
        //使用Java代码，在服务器做消息过滤
//        String filterCode = MixAll.file2String ("/ home/ admin/ MessageFilterimpl . java " );
        return false;
    }

    public void test(){
        Runtime.getRuntime().addShutdownHook(new ShutDownHootThreadTest("", new Callable() {
            @Override
            public Object call() throws Exception {
                //自定义逻辑，比如程序结束的时候关闭某个接口
                return null;
            }
        }));
    }
}

/**
 * 定义钩子线程
 */
class ShutDownHootThreadTest extends Thread{
    private Callable callable;
    private String log;

    public ShutDownHootThreadTest(String log,Callable callable){
        this.log = log;
        this.callable = callable;

    }
    @Override
    public void run() {
        super.run();
    }
}

```
