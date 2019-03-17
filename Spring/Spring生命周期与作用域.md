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

#### Spring的作用域
1. Singleton，这是 Spring 的默认作用域，也就是为每个 IOC 容器创建唯一的一个 Bean 实例。
2. Prototype，针对每个 getBean 请求，容器都会单独创建一个 Bean 实例。
3. Request，为每个 HTTP 请求创建单独的 Bean 实例。
4. Session，很显然 Bean 实例的作用域是 Session 范围。
5. GlobalSession，用于 Portlet 容器，因为每个 Portlet 有单独的 Session，GlobalSession提供一个全局性的 HTTP Session。

![Spring AOP](https://github.com/ljcan/Review/blob/master/Java/pictures/Sprinng%20APO.png)
