```
package mertic;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MerticTest {

    static Random random = new Random();
    public static void request(Meter meter){
//        System.out.println("request");
        meter.mark();
    }

    public static void request(Meter meter,int n){
        while (n>0){
            request(meter);
            n--;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MetricRegistry metricRegistry = new MetricRegistry();
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry). build();
        consoleReporter.start(1, TimeUnit.SECONDS);
        Meter meter = metricRegistry.meter(
                MetricRegistry.name(MerticTest.class, "request", "tps"));

        while (true){
            request(meter,random.nextInt());
            Thread.sleep(1000);
        }

    }
}
```
