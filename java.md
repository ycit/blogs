## JAVA

### Java 内存模型（JMM）

JMM 是一种规范，规范了 Java 虚拟机 与 计算机内存是如何协同工作的。一个线程对共享变量的写入何时对另一个线程可见。

首先说下 Java 内存划分

- 线程共享数据区

  > 1. 方法区
  >
  >    > 存储已被虚拟机加载的**类型信息、常量、静态变量、即时编译器编译后的代码缓存**
  >    >
  >    > **运行时常量池**：方法区的一部分；class 文件中除了有类的版本、字段、方法、接口等描述信息外，还有一项信息是常量池表，用于存储编译期生成的各种字面量与符号引用，这部分加载后存放到运行时常量池。
  >
  > 2. Java 堆  
  >
  >    > 存放对象实例
  >    >
  >    > 新生代：eden区，两个 survivor 区：S0,S1; 默认比例 8:1:1 ，默认年龄达到15 或者 同一个年龄的数量超过 survivor 区一半时也会进行回收；正常对象先放入新生代的 egen 区，如果创建的对象太大，eden 放不下，则会直接放到老年代
  >    >
  >    > 老年代：

- 线程隔离数据区

  > 1. 程序计数器
  >
  > 2. Java虚拟机栈  stack
  >
  >    > 每个方法被执行的时候，Java 虚拟机都会同步创建一个栈帧用于存储 **局部变量表**，**操作数栈**，**动态链接**，**方法出口**。每个方法被调用直至执行完毕的过程，就对应着一个栈帧在虚拟机栈中从**入栈到出栈**的过程
  >    >
  >    > 局部变量表：存放 编译期可知的 Java 虚拟机**基本数据类型**、**对象引用** 和  **returnAddress 类型**；其中 double、long 占 2 个 slot ，其余均占 1个 slot。局部变量表所需的内存空间在编译期间完成分配。
  >    >
  >    > 这个内存区域规定了两类异常状况：
  >    >
  >    > - 如果线程请求的栈深度大于虚拟机所允许的深度，将抛出 **StackOverflowError** 异常
  >    > - 如果 Java 虚拟机栈容量可以动态扩展，当栈扩展时无法申请到足够的内存会抛出 **OutofMemoryError**异常（Hotspot 虚拟机不支持动态扩展）
  >
  > 3. 本地方法栈
  >
  >    > 为虚拟机使用到的本地方法服务

每个线程都会有自己的工作内存，是线程私有的；另外 JMM 规定 所有的变量都存储到主内存中。工作内存中的变量是 主内存中变量的拷贝，操作完之后会写回到主内存，不能直接操作主内存。这样在多线程的情况下就会造成线程不安全的问题。JMM 是围绕 程序执行的 原子性、有序性、可见性展开的。

Java 提供了 Sychronized 关键字 或者 ReentrantLock 保证程序执行的原子性；

可见性问题可以通过 Sychronized 或者 volatile 关键字解决；

volatile 可以保证原子性，以及禁止指令的重排序；

另外 JMM 内部还定义了一套 happens-before 原则来保证多线程环境下两个操作间的可见性、有序性。

### IO

- BIO：同步阻塞 IO

  面向流，socket 的 accept 、read、write 等都是同步阻塞的，如果需要同时处理多个客户端，就需要开启多个线程

- NIO：同步非阻塞 IO

  支持面向缓冲的、基于通道的 IO 操作方法。

  核心api：channel、Buffer、Selector

  > - channel
  >
  > FileChannel：从文件中读取数据
  >
  > DatagramChannel：从UDP网络中读取或者写入数据
  >
  > SocketChannel：从TCP网络中读取或者写入数据
  >
  > ServerSocketChannel：允许你监听来自TCP的连接，就像服务器一样。每一个连接都会有一个SocketChannel产生。
  >
  > - buffer
  >
  > ByteBuffer：字节缓冲区
  >
  > CharBuffer：字符缓冲区
  >
  > ShortBuffer：
  >
  > IntBuffer
  >
  > LongBuffer：
  >
  > FloatBuffer：
  >
  > DoubleBuffer
  >
  > - selector
  >
  > 多路复用的 selector,selector选择器可以监听多个Channel通道感兴趣的事情(read、write、accept(服务端接收)、connect，实现一个线程管理多个Channel，节省线程切换上下文的资源消耗。Selector只能管理非阻塞的通道，FileChannel是阻塞的，无法管理。
  >
  > 代表框架：
  >
  > netty

| I/O方式             | 系统调用   | CPU拷贝次数 | DMA拷贝次数 | 上下文切换次数 |
| ------------------- | ---------- | ----------- | ----------- | -------------- |
| 传统I/O             | Read/Write | 2           | 2           | 4              |
| 内存映射            | Mmap/write | 1           | 2           | 4              |
| sendfile            | sendfile   | 1           | 2           | 2              |
| sendfile+DMA gather | sendfile   | 0           | 2           | 2              |
| splice              | splice     | 0           | 2           | 2              |
| tee                 | tee        | 0           | 2           | 2              |

DMA：direct memory acess，直接存储器访问

### 

