根据分代回收的基本思路是根据对象的生存时间长短，把堆内存分为三代：
- Young（新生代）
- Old（老年代）
- Permanent（永生代）

**Young代**

由一个Eden区和两个Survivor区构成，使用复制算法进行垃圾回收，一般对象分配内存时首先会分配进Eden区（一些大对象可能会被分配到Old代中），当Eden区和一个Survivor区
内存分配满了之后会将存活的对象复制到另一个Survivor区中，当Young代快满的是会触发一次minor GC,将一些存活时间够长的对象移到Old代中。

Eden和Survivor区的比例默认是8:1:1，可以通过`-XX:SurvivorRatio`附加选项来设置，默认是32。

**Old代**

老年代中的对象生存时间较长，不容易“死去”，因此GC时间不需要很频繁，因此，老年代中垃圾回收使用`标记整理法`。当Young代和Old代中的内存都快被占满的时候，会触发
一次major GC,此次垃圾回收会“Stop the world”，而且执行时间较长，因此，要尽量避免发生major GC。

**Permanent代**

永久代主要用于装载Class，方法等信息，默认64MB，垃圾回收机制通常不会回收永久代中的对象，对于一些需要加载很多类的服务器程序，往往需要加大Permanent代的内存
，否则可能会导致内存不足而程序终止。

**与垃圾回收相关的参数选项**

- -Xmx：设置java虚拟机堆内存的最大容量，如java -Xmx256m XxxClass
- -Xms：设置java虚拟机堆内存的初始容量。
- -XX:MinHeapFreeRatio=40：设置java堆内存最小的空闲百分比，默认值为40
- -XX:MaxHeapFreeRatio=70：设置java堆内存最大的空闲百分比，默认值为70
- -XX:NewRatio=2：设置Young/Old内存比例
- -XX:NewSize=size：设置Young代内存的默认容量。
- -XX:SurvivorRatio=8：设置Young代中Eden/Survivor的比例
- -XX:MaxNewSize=size：设置Young代内存的最大容量
- -XX:PermSize=size：设置Permanent代内存的默认容量
- -XX:MaxPermSize=64m：设置Permanent代内存的最大容量。
