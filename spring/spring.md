## 事务

1. 只有定义在 public 方法上的 @Transactional 才能生效

   > spring 默认通过动态代理的方式（JDK动态代理 + Cglib 动态代理(也叫子类代理)）实现 AOP，对目标方法进行增强，private 方法无法代理到，而事务是通过 AOP 的方式实现的，所以事务无法生效

2. 必须通过代理过的类从外部调用目标方法才能生效

   > spring 通过 aop 实现事务，调用内部方法不会走 动态代理对象，也就不会生效
   >
   > 解决方法：
   >
   > - 注入自身后，用注入的对象调用内部方法
   > - 开启 exposeProxy， 使用 AopContext.currentProxy() 获取当前代理对象后再调用内部方法
   > - 直接在controller 层调用注解的方法

3. 1

