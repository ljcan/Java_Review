ArrayBlockQueue底层基于数组实现，底层用一个final修饰数组来装载数据，因此其大小固定：
```
    /** The queued items */
    final Object[] items;
```

除此之外，它使用重入锁来保证线程的安全性。
它实现简单，性能比LinedBlockQueue更加稳定。


LinkBlockQueue底层基于链表实现，因此其是无界队列。

它使用类似读写锁的机制来保证读写线程的安全性：
```
 /** Lock held by take, poll, etc */
    private final ReentrantLock takeLock = new ReentrantLock();

    /** Wait queue for waiting takes */
    private final Condition notEmpty = takeLock.newCondition();

    /** Lock held by put, offer, etc */
    private final ReentrantLock putLock = new ReentrantLock();

    /** Wait queue for waiting puts */
    private final Condition notFull = putLock.newCondition();
```
因此其吞吐量要高于ArrayBlockQueue。
