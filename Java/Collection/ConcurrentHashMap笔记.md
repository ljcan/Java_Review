在JDK1.8之前的JDK中，ConcurrentHashMap使用Segment分段锁的思想来保证线程安全性，其中Segment元素是实现了ReentrantLock。
其中get()方法不需要加锁，因为get方法中的共享变量都定义成了volatile类型，保证可以被多个线程可见，并且不会读到过期的数据。

put方法时首先会定位到某一个Segment，然后在Segment里进行插入操作，插入操作需要经历两个步骤，第一步判断是否需要对Segment里的HashEntry数组进行扩容，
第二步定位添加元素的位置，然后将其放在HashEntry数组里。

ConcurrentHashMap的扩容比HashMap更加适当，因为hashMap是在插入元素之后判断元素是否已经到达容量，如果到达了容量就进行扩容，但是很有可能扩容之后没有新
的元素插入，这时就是一次无效的扩容。而ConcurrentHashMap是在put之前就判断进行扩容。

size方法中并没有将put,remove和clean方法全部锁住，而是两次尝试不加锁判断count的值是否被修改过，如果没有修改过则表示不加锁统计成功，如果`modcount!=count`
表示发生了修改，此时才会加锁计算count的值。


在JDK1.8中，ConcurrentHashMap摒弃了Segment锁的思想，使用了CAS无锁的方式更加高效。
使用Node数组来保存元素，在put操作时，首先它会判断key与value是否为空，为空则抛出异常。
如果table为空，则会调用`initTable`方法初始化table，否则则通过tabAt方法获取对应的Node，如果node为空，则使用casTabAt方法来设置node值，如果成功则返回。
否则则判断是否需要扩容，最后使用synchronized锁的方式锁住node来进行插入操作。
```
 public V put(K key, V value) {
        return putVal(key, value, false);
    }

    /** Implementation for put and putIfAbsent */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }
```
