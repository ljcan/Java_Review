NFA是建立在流处理之上的一个状态机，它以流中的事件作为输入并驱动状态的转换，Flink流式处理的高效转换和状态的管理完美的实现了NFA。

Flink CEP在运行时会将用户的逻辑转化为一个NFA Graph（NFA对象），graph中包含中间状态（Flink中的state对象），以及连接状态的边（Flink中的StateTransition对象）。

当从一个state状态转换到另一个state状态，就需要经过一个包含Conition对象，其中包含逻辑，也就是代码中where的方法；Condition对象中包含是否可以完成状态跳变的条件，A状态要跳转到B状态就必须满足连接AB的边中的条件。

StateTransition分三种：
1. take：满足状态转换条件直接转换。
2. igonre：不满足转换条件，回到原来状态
3. process：这条边可以忽略也可以不忽略。
