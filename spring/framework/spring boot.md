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

