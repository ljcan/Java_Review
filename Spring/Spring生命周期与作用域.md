#### Spring的生命周期

1. 初始化Bean
2. 设置Bean属性
3. 如果通过各种Aware接口声明了依赖关系，则会注入Bean对容器基础设施的依赖。分别会注入Bean ID,Bean Factory,Application Context。
4. 调用BeanPostProcessor的前置初始化方法postprocessorBeforeInitialization。
5. 如果实现了 InitializingBean 接口，则会调用 afterPropertiesSet 方法。
6. 调用 Bean 自身定义的 init 方法。
7. 调用 BeanPostProcessor 的后置初始化方法 postProcessAfterInitialization。
8. 创建过程完毕。
![Spring Bean初始化](https://github.com/ljcan/Review/blob/master/Java/pictures/%E6%90%9C%E7%8B%97%E6%88%AA%E5%9B%BE20190317150626.png)

Spring Bean 的销毁过程会依次调用 DisposableBean 的 destroy 方法和 Bean 自身定
制的 destroy 方法。
