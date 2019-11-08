## 线程 Thread

### 基本概念

> 线程是操作系统能够进行运算调度的最小单位。它被包含在进程中，是进程中的实际运作单位。一条线程指的是进程中一个单一顺序的控制流，一个进程中可以并行多个线程，每条线程并行执行不同的任务。

### 实现方式

- 实现 Runnable 接口
- 继承 Thread 类
- 实现 Callable 接口

```java
package com.ycit.thread.impl;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author chenxiaolei
 * @date 2019/4/24
 */
public class ThreadTest {

    class RunnableImpl implements Runnable {
        @Override
        public void run() {
            System.out.println(1);
        }
    }

    class ThreadImpl extends Thread {
        @Override
        public void run() {
            System.out.println(2);
        }
    }

    class CallableImpl implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            int sum = 2;
            return ++sum;
        }

    }

    @Test
    public void test() {
        //Runnable
        Thread t1 = new Thread(new RunnableImpl());
        t1.start();
        // Thread
        Thread t2 = new ThreadImpl();
        t2.start();
        // callable :  特点，可以抛出异常,也可以获取线程执行的结果
        Callable<Integer> one = new CallableImpl();
        FutureTask<Integer> future = new FutureTask<>(one);
        Thread t3 = new Thread(future);
        t3.start();
        try {
            System.out.println(future.get());;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}

```

### 线程状态

- 新建状态 new：新创建的线程，还未执行 start 方法
- 可运行状态 runnable：正在虚拟机中运行，已获取相关锁，但是可能正在等待来自操作系统的资源，如 CPU
- 阻塞状态 blocked：正在等待监视器锁的线程。调用 Object 的 wait 方法，之后线程进入 等待状态，当其他线程中调用了 锁对象的 notify 方法 或者任意一个对象的 notifyAll 方法，则 线程苏醒，此时，若锁对象的锁正在被其他线程使用，则 线程处于 阻塞状态，否则进入 可运行状态，等待 操作系统资源
- 等待状态 waiting： Object 的 wait()，Thread.join()，LockSupport.park()方法调用后，进入等待状态
- 指定时间的等待状态 timed_waiting：Object 的 wait(time)，Thread.join(time)，LockSupport.parkNanos
- 消亡状态 terminated

### 线程安全的实现方法

- 互斥同步（悲观锁）：互斥是因，同步是果，临界区、互斥量和信号量 是实现互斥的实现方式

  > synchronized 关键字修饰，synchronized 关键字经过编译之后，会在同步块的前后分别形成 monitorenter 和 monitorexit 这两个字节码指令，这两个字节码都需要一个 reference 类型的参数来指明要锁定和解锁的对象。在执行 monitorenter 时，首先尝试获取对象的锁，如果这个对象没有被锁定，或者当前线程已经拥有了那个对象的锁，把锁的计数器加1，相应的，在执行 monitorexit 时会将锁计数器减1，当计数器为0时，锁就被释放。如果获取对象锁失败，那么当前线程就要阻塞等待，直到对象锁被另外一个线程释放为止。
  
- 非阻塞同步（乐观锁）

  > 通过硬件保证一个从语义上看起来需要多次操作的行为只通过一条处理器指令就能完成。包括：
  >
  > 1. 测试并设置
  >
  > 2. 获取并增加
  >
  > 3. 交换
  >
  > 4. 比较并交换（CAS）:JDK1.5后支持，会有 ABA 问题

### 锁优化

jdk1.5 到 jdk1.6 对锁的优化，包括：

- 自适应性自旋

  > jdk1.4 中实现了自旋锁，默认关闭；jdk1.6 实现了 适应性自旋锁，默认打开；
  >
  > 自适应自旋：自旋时间不再固定，由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定

- 锁清除

  > 虚拟器在即时编译器运行时，对一些代码上要求同步，但是被检测到不可能存在共享数据竞争的锁进行消除。通过逃逸分析技术判断是否存在共享数据竞争。

- 锁粗化

  > 原则上，总是推荐将同步块的作用范围限制得尽量小，只在共享数据的实际作用域中才进行同步；但是如果一系列的连续操作都对同一个对象反复加锁和解锁，甚至加锁操作是出现在循环体中，这样会造成不必要的性能锁耗，虚拟机会把加锁同步的范围扩展（粗化）到整个操作序列的外部，这样只需要加一次锁就可以了。

- 轻量级锁

  > 轻量级锁是在无竞争的情况下使用 CAS 操作去消除同步使用的互斥量。如果锁竞争严重，不建议开启。
  >
  > 虚拟机的对象头（Object Header）分为两部分：
  >
  > 第一部分用于存储对象自身的运行时数据，如哈希码，GC分代年龄，称为 Mark Word；32位和64位的mark word 分别为 32bit 和 64bit；若为32bit，在未被锁定状态下 25bit 用来存储hashcode，4bit存储对象分代年龄，2bit存储锁标志位，1bit固定为0；
  >
  > 其他状态下为以下存储内容
  >
  > | 存储内容                            | 锁标志位 | 状态               |
  > | ----------------------------------- | -------- | ------------------ |
  > | 哈希码，对象分代年龄                | 01       | 未锁定             |
  > | 指向锁记录的指针                    | 00       | 轻量级锁定         |
  > | 指向重量级锁的指针                  | 10       | 膨胀（重量级锁定） |
  > | 空，不需要记录信息                  | 11       | GC标记             |
  > | 偏向线程ID,偏向时间戳，对象分代年龄 | 01       | 可偏向             |
  >
  > 另一部分用于存储指向方法区对象类型数据的指针，如果是数组对象的话，还会有一个额外的部分用于存储数组长度。
  >
  > 轻量级锁加锁流程：
  >
  > 1. 代码进入同步块，如果**此同步对象没有被锁定**（标志位为 01），虚拟机首先将在当前线程的栈帧中建立一个名为锁记录（Lock Record）的空间，用于存储对象目前的 Mark Word的拷贝，然后虚拟机将使用 CAS 操作尝试将对象的 Mark Word 更新为指向 Lock Record 的指针，如果**更新成功**了，那么该线程就拥有了该对象的锁，并且对象 Mark Word 的锁标志位将转变为 00，即表示此对象处于轻量级锁定状态。如果**更新失败**，虚拟机会检查对象的 Mark Word 是否指向当前线程的栈帧，如果是则说明**当前线程已经拥有了这个对象的锁**，就可以直接进入到同步代码块，**否则**说明这个锁对象已经被其他线程抢占了。如果有两条以上的线程争用同一个锁，那轻量级锁就不再有效，要膨胀为重量级锁，锁标志位状态值变为 10，Mark Word 中存储的就是 重量级锁的指针，后面等待锁的线程也要进入阻塞状态。
  >
  > 轻量级锁解锁流程：
  >
  > 1. 如果对象的 Mark Word 仍然指向着线程的锁记录，那就用 CAS 操作把对象当前的 Mark Word 和线程中复制的 Displaced Mark Word 替换回来，如果替换成功，整个同步过程就完成了；如果替换失败，说明有其他线程尝试过获取该锁，那就在释放锁的同时，唤醒被挂起的线程。

- 偏向锁

  > 偏向锁是在无竞争的情况下把整个同步都消除掉，连 CAS 操作都不做了。若果锁竞争比较严重，则不建议开启。
  >
  > 如果当前 虚拟机启用了偏向锁，那么当锁对象第一次被线程获取时，虚拟机会把对象头中的标志位设为 01，即偏向模式。同时使用 CAS 操作把获取到这个锁的线程 ID 记录在对象的 Mark Word 之中。如果 CAS 操作成功，持有偏向锁的线程以后每次进入这个锁相关的同步块时，虚拟机都可以不再进行任何同步操作。当有另外一个线程去尝试获取这个锁时，偏向模式就宣告结束。根据锁对象目前是否处于被锁定的状态，撤销偏向后恢复到未锁定（01）或轻量级锁定（00）的状态。后续操作和轻量级锁的流程一样

### wait 和 sleep 的区别

- wait 是 Object 类中的方法，是非静态方法；sleep 是在当前线程上操作并且方法是定义在 Thread 类中，是静态方法。
- wait 方法必须在同步上下文中调用，比如在同步代码块 或者 同步方法中调用，否则会抛出 IllegalMonitorStateException  异常；sleep 不需要。
- wait 方法会释放其调用的对象锁，如果它持有其他锁，同时释放其他锁； sleep 方法 不会释放任何锁。

### 线程池

通过 ThreadPoolExecutor 类创建线程池，参数

- corePoolSize：线程池中的核心线程数，即空闲状态下仍然保持的最小线程数（除非设置了allowCoreThreadTimeOut 为 true）；

- maximumPoolSize：线程池中允许的最大线程数；

- keepAliveTime：当线程数大于核心线程数时，空闲线程等待时间超过该设置时间将时将被终止；

- TimeUnit：时间单位

- BlockingQueue：阻塞队列，用来存储等待执行的任务，以下几种选择

  > ArrayBlockingQueue：基于数组实现的阻塞队列
  >
  > LinkedBlockingQueue：基于 链表实现的 先入先出的 阻塞队列
  >
  > SynchronousQueue：该阻塞队列没有内部容量

  |             | *Throws exception*                                           | *返回特殊的值*                                               | *Blocks*                                                     | *Times out*                                                  |
  | ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
  | **Insert**  | [`add(e)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#add-E-) | [`offer(e)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#offer-E-) | [`put(e)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#put-E-) | [`offer(e, time, unit)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#offer-E-long-java.util.concurrent.TimeUnit-) |
  | **Remove**  | [`remove()`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#remove-java.lang.Object-) | [`poll()`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#poll-long-java.util.concurrent.TimeUnit-) | [`take()`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#take--) | [`poll(time, unit)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#poll-long-java.util.concurrent.TimeUnit-) |
  | **Examine** | [`element()`](https://docs.oracle.com/javase/8/docs/api/java/util/Queue.html#element--) | [`peek()`](https://docs.oracle.com/javase/8/docs/api/java/util/Queue.html#peek--) | *not applicable*                                             | *not applicable*                                             |

- ThreadFactory：线程工厂，主要用来创建线程

- RejectedExecutionHandler：当拒绝处理任务的策略，有以下四种

  >ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。 
  >ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。 
  >ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
  >ThreadPoolExecutor.CallerRunsPolicy：由调用线程处理该任务 

Executors 工具类提供了几种线程池的创建：

- Executors .newFixedThreadPool(int nThreads)：固定大小的线程池

  > ```java 
  > new ThreadPoolExecutor(nThreads, nThreads,
  >                               0L, TimeUnit.MILLISECONDS,
  >                               new LinkedBlockingQueue<Runnable>())
  > ```

- Executors .newSingleThreadExecutor()：单线程

  > ```java
  >  new FinalizableDelegatedExecutorService
  >     new ThreadPoolExecutor(1, 1,
  >                             0L, TimeUnit.MILLISECONDS,
  >                             new LinkedBlockingQueue<Runnable>())
  > ```

- Executors.newCachedThreadPool()：核心线程数为0，最大线程数为无穷大；无边界线程池

  > ```java
  > new ThreadPoolExecutor(0, Integer.MAX_VALUE,
  >                               60L, TimeUnit.SECONDS,
  >                               new SynchronousQueue<Runnable>())
  > ```

- Executors.newScheduledThreadPool(int corePoolSize)：定时任务线程池

  > ```java
  > super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
  >       new DelayedWorkQueue())
  > ```

### 线程池源码解读

ThreadPoolExecutor 类自顶向下的继承关系: j.u.c 包

```json
Executor:顶层接口，抽象了 execute(Runnable) 方法
ExecutorService: 提供了管理线程终止的方法,以及返回Future(便于异步任务跟踪)的方法
AbstractExecutorService:提供了几个方法默认实现类的抽象类
ThreadPoolExecutor:使用线程池来执行提交的任务
```

ThreadPoolExecutor 内部类 Worker  自顶向下的继承关系:  j.u.c.locks 包

```java
AbstractOwnableSynchronizer: 可以被线程独占的同步器
AbstractQueuedSynchronizer: AQS 提供一个框架，用于实现依赖先进先出（fifo）等待队列的阻塞锁和相关同步器（信号量、事件等）,内部使用 CLH 锁队列实现，是一种自旋锁。
Worker:同时实现了 Runnable,
```

> CAPACITY = (1 << 29) - 1  即 高3位为0，低 29位为1

####　ctl

> 线程池用于控制状态的属性，是 Integer 的原子操作类，32位；自身携带以下两个字段的信息：
>
> workerCount: 有效线程数； 最大可存储 2^29-1 个线程，即存储在低 29位中
>
> ​       workerCount  获取： c & CAPACITY  逻辑与后即只保留低29位
>
> runState: 运行状态，存储在高3位中
>
> 有以下几种状态
>
> - Running: 接收新任务，同时处理队列中的任务； -1 << 29 ：即高3位为1，低29位为0
>
> - shutdown: 不接收新任务，处理队列中的任务；0<<29：即高3位为0，低29位为0
>
> - stop: 不接收新任务，不处理队列中的任务；1<<29：即高3位为001，低29位为0
>
> - tidying（整理）: 所有的任务都终止了，workerCount 为0，所有线程为整理状态，将运行终止的hook 方法；2<<29：即高3位为010，低29位为0
>
> - terminated:  terminated() 方法已经完成；3<<29：即高3位为011，低29位为0
>
>    大小关系为 RUNNING<SHUTDOWN<STOP<TIDYING<TERMINATED
>
> ​    runState 获取：c & ~CAPACITY   ；~CAPACITY 即 高3位为1，低29位为0，逻辑与后只保留高3位的状态

-1,0,1,2,3 对应的二进制表示

![-1,0,1,2,3 二进制表示](D:\study\blogs\images\threadpoolexecutorctl.webp)

左移29位后的二进制表示

![](D:\study\blogs\images\runstate2.webp)

转为十进制后为

```text
RUNNING = -536870912
SHUTDOWN = 0
STOP = 536870912
TIDYING = 1073741824
TERMINATED = 1610612736
```

~CAPACITY 的二进制表示为

```text
CAPACITY = 00011111111111111111111111111111
~CAPACITY = 11100000000000000000000000000000
```



execute 方法执行流程图：

![执行流程图](D:\study\blogs\images\ThreadPoolEexcutor-execute.png)

execute 方法执行逻辑：

- 如果当前运行的线程数，少于 corePoolSize，则尝试创建一个新的线程（worker）来执行任务
- 如果当前运行的线程数，大于或者等于 corePoolSize，但队列 workQueue 未满，则新添加的任务放到 workQueue 中
- 如果当前运行的线程数，大于或者等于 corePoolSize，且队列 workQueue 已满，但线程池中的运行线程数少于 maximumPoolSize，则会创建新的线程来执行任务
- 如果当前运行的线程数，大于或者等于 corePoolSize，且队列 workQueue已满，并且 运行的线程数 等于 maximumPoolSize，则使用 RejectExecutionHandler 来执行拒绝策略。

BlockingQueue (阻塞队列)：

> 是一种队列，在队列的基础上还增加了其他功能，如在检索元素时等待队列变为非空，在存储元素时等待队列中空间可用。 BlockingQueue 有四种形式处理不能立即满足操作的情形
>
> 1. 抛出异常
> 2. 方法返回特殊值(null 或者 false)
> 3. 无限期的阻塞当前线程直到操作成功
> 4. 在给定的最大时间内等待，否则超时
>
> |             | Throws exception                                             | Special value                                                | Blocks                                                       | Times out                                                    |
> | ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
> | **Insert**  | [`add(e)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#add-E-) | [`offer(e)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#offer-E-) | [`put(e)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#put-E-) | [`offer(e, time, unit)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#offer-E-long-java.util.concurrent.TimeUnit-) |
> | **Remove**  | [`remove()`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#remove-java.lang.Object-) | [`poll()`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#poll-long-java.util.concurrent.TimeUnit-) | [`take()`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#take--) | [`poll(time, unit)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#poll-long-java.util.concurrent.TimeUnit-) |
> | **Examine** | [`element()`](https://docs.oracle.com/javase/8/docs/api/java/util/Queue.html#element--) | [`peek()`](https://docs.oracle.com/javase/8/docs/api/java/util/Queue.html#peek--) | *not applicable*                                             | *not applicable*                                             |
>
> BlockingQueue 不支持 null 值，如果插入 null 值 会抛出异常；
>
> BlockingQueue 支持容量限制，当超过给定的容量，即时没有阻塞也不能插入任何元素；
>
> BlockingQueue 主要用于生产者-消费者队列；
>
> BlockingQueue 是线程安全的。所有的入队操作通过使用内部锁或者其他形式并发控制原子操作；
>
> BlockingQueue 本质上

### 死锁

> 两个或两个以上的进程在执行过程中，由于竞争资源或者由于彼此通信而造成的一种阻塞的现象，若无外力作用，它们都将无法推进下去

#### 死锁产生的条件

- 互斥条件：进程对分配的资源独占，添加 排它锁 （ 独占锁）
- 请求与保持条件：在保持了至少一个资源的同时，又提出了新的资源请求
- 不剥夺条件：获得的资源在未使用完毕之前，不能被其他进程强行夺走
- 循环等待条件：

### synchronize volatile 区别

并发中关注的三个问题：

- 原子性
- 可见性
- 有序性

#### synchronize

重量级的排它锁，被 synchronize 修饰的代码块具有原子性。jdk1.6 之后优化：

- 自旋锁：资源互斥不挂起线程，执行一个忙等待
- 

#### volatile

 ==volatile== 保证可见性，不保证原子性。

jdk5之后被 ==volatile==修饰的共享变量，具备两层语义：

- 保证不同线程对这个变量的可见性，即一个线程修改了该变量，会直接刷新到主存，并使缓存失效，另一个线程会直接读取主存中的值
- 禁止重排序

### Java 内存结构

- 方法区：==所有线程共享==，存储每个类的结构，例如运行时常量池、字段、方法、构造函数等
- 堆：==所有线程共享==，用于分配所有的类实例 和 数组，GC管理区域。
- Java栈：线程私有，每个 JVM 线程在创建时都会创建一个私有的 JVM 栈，用于保存堆的地址。如果线程请求的深度大于虚拟机所允许的深度，抛出 StackOverflowError.
- 程序计数器：线程私有，用于记录当前 JVM 指令执行的地址。
- 本地方法栈：线程私有，用来支持 native 方法

### JAVA 内存模型（JMM）

站在程序员的视角，本质上可以理解为，==Java 内存模型规范了 JVM 如何提供按需禁用缓存和编译优化的方法。==具体来说，这些方法包括：

- volatile
- synchronized
- final
- Happens-Before

#### Happens-Before

1. 程序的顺序性规则：指在一个线程中，按照程序顺序，前面的操作 Happens-Before 于后续的任意操作
2. volatile 规则：指对一个 volatile 变量的写操作，Happens-Before 于后续对这个 volatile 变量的读操作
3. 传递性：指如果 A Happens-Before B，且 B Happens-Before C，那么 A Happens-Before C
4. 管程中锁的规则：对一个锁的解锁 Happens-Before 于后续对这个锁的加锁
5. 线程 start() 规则：指主线程 A 启动子线程 B 后，子线程 B 能够看到主线程在启动子线程 B 前的操作
6. 线程 join() 规则：指主线程 A 等待子线程 B 完成（主线程 A 通过调用子线程 B 的 join() 方法实现），当子线程 B 完成后（主线程 A 中 join() 方法返回），主线程能够看到子线程的操作。看到是指 对共享变量的操作。

#### 主内存和工作内存

JMM 规定所有的变量都存储在主内存，每个线程还有自己的工作内存，线程在工作内存中保存了该线程使用到的变量的主内存的副本拷贝，线程对变量的所有操作都必须在工作内存中进行。不同的线程之间也无法直接访问对方工作内存中的变量，线程之间值的传递都需要主内存来完成。

RAM-主内存，CPU高速缓存，CPU；

所有的变量都存储在主内存，CPU 高速缓存中持有主内存中变量的拷贝，Java 线程 和 CPU 高速缓存交换数据。

没有被 volatile 关键字修饰的变量，每个线程都会在使用到这个变量时从主内存拷贝这个变量到 CPU 高级缓存中；

被 volatile 关键字修饰的变量，所有写入到该变量的值会被立即写到主内存中，并且会使高速缓存中的变量值失效，线程读取这个变量时会直接从主内存中读取。

[memory](file:///D:/study/blogs/java-volatile-1.png)

线程1和线程2要想进行数据交换一般经历下面的步骤：

- 线程1 把 工作内存1 中 更新过的共享变量刷新到主内存中
- 线程2 到主内存中读取线程1刷新的共享变量

#### 原子性

```java
public volatile int count = 0;(1)
count++;(2)
```

对于（2），可以分解为3个步骤

1. 将 count 值从主内存加载到 cpu 的某个寄存器r
2. 将寄存器 r 的值 +1，结果存放到寄存器 s
3. 将寄存器 s 中的值写回到内存

#### 可见性

#### 有序性

Happens-Before 原则：如果在时间上，动作A发生在动作B之前，并且 B 可以看见 A 的影响，那么就可以说 hb(A,B)。

JVM 保证了如下HB：

- 如果 A 和 B 是同一个线程，并且 A 发生在 B 之前，则 hb(A,B)
- 如果 A 是对锁的 unlock，B 是对锁的 lock，那么 hb(A,B)
- 如果 A 是对 volatile 变量的写操作，B 是对同一个变量的读操作，那么 hb(A,B)
- 如果 hb(A,B) 且 hb(B,C)，那么 hb(A,C)

happens-before 规则：

- 程序顺序规则：一个线程中的每个操作，happens-before于该线程中的任意后续操作。 
- 监视器锁规则：对一个锁的解锁，happens-before于随后对这个锁的加锁。 
- volatile变量规则：对一个volatile域的写，happens-before于任意后续对这个volatile域的读。
- 传递性：如果A happens-before B，且B happens-before C，那么A happens-before C。 
- start()规则：如果线程A执行操作ThreadB.start()（启动线程B），那么A线程的ThreadB.start()操作happens-before于线程B中的任意操作。 
- join()规则：如果线程A执行操作ThreadB.join()并成功返回，那么线程B中的任意操作happens-before于线程A从ThreadB.join()操作成功返回。



## AQS

> AQS 即 AbstractQueuedSynchronizer

### java doc

> AQS 提供了一个框架，这个框架实现了阻塞锁 和 依赖于先进先出(FIFO)等待队列的相关同步器(信号量，事件等)。
>
> 这个类被设计为有用的基础类，提供给依赖于通过单个原子 int 值来代表状态的大多数类型的同步器使用。子类必须定义 protected类型的 方法以便改变 state 值，
>
> AQS 类支持默认的排它模式和共享模式。以独占模式获取锁时，其他线程尝试获取锁是无法获取成功的。共享模式多线程获取锁时，	可能(但不需要)成功。
>
> AQS 定义了一个 ConditionObject 内部类











