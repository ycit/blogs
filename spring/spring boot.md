## spring boot

### 自动装配原理

1. spring boot 使用 SpringBootApplication 注解开启自动装配，该注解为组合注解

   - Configuration
   - ComponentScan
   - EnableAutoConfiguration

2. EnableAutoConfiguration 注解 开启自动装配

   - AutoConfigurationPackage 注解

     - import 注入 AutoConfigurationPackages.Registrar 类: 存储 base package

   - import 注解 注入  EnableAutoConfigurationImportSelector 类(1.5之后废弃)

     父类 AutoConfigurationImportSelector   的 selectImports 方法： 这里会加载META-INF/spring.factories文件中配置的自动装配类，根据conditional 注解选择性加载



## feign

### 原理

1. @EnableFeignClients   开启feign

   >  其上有个 @Import 注解 注入 FeignClientsRegistrar 类 ，该类用于注册标注了 @FeignClient 注解的接口

2. @FeignClient  标注类为feign 客户端

3. 启动过程中，spring 将 所有标注了 @FeignClient 注解的 接口 注入到  ioc 容器中

   > spring 会在启动过程的 invokeBeanFactoryPostProcessors 方法中（在上下文中,将工厂处理器 注册 为 bean），其中便会调用 FeignClientsRegistrar  类中的方法
   >
   > spring 会在启动过程的  finishBeanFactoryInitialization   方法中 （倒数第二步，实例化所有剩余的单例）,调用 feignClient 的 工厂方法 FeignClientFactoryBean 构建 feign， 这个过程中会为每个feignClient 加载各自的 配置，包括
   >
   > - RequestInterceptor ：请求拦截器，包括标注了 @Configuration注解的全局拦截器 和 通过FeignClient 的 configuration 属性注入的私有的拦截器；如果name 相同，配置可能会被覆盖
   > - logLevel：日志等级
   > - Contract
   > - Retryer：重试机制
   > - logger
   > - encoder
   > - decoder
   > - errorDecoder
   > - decode404
   >
   > 最终会调用 Feign 的 newInstance 方法，使用 JDK 的动态代理 实例化。然后添加到 IOC 容器中。

4. 调用过程中，注入的 feignClient  其实是一个 动态代理类 ，会调用 ReflectiveFeign.FeignInvocationHandler 的 invoke 方法进行转发。