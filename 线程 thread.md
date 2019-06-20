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
        Thread thread = new Thread(future);
        thread.start();
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

### 线程安全

各种操作共享的数据可以分为：

1. 不可变：不可变对象一定是线程安全的；如果共享数据是一个基本数据类型，final 修饰就可以保证是不可变的；如果是一个对象，必须保证行为不会对其状态产生任何影响。如 String ,Integer
2. 绝对线程安全
3. 相对线程安全: Vector ,HashTable 等
4. 线程兼容
5. 线程对立：无论如何都无法保证线程安全

### 线程安全的实现方法

- 互斥同步：互斥是因，同步是果，临界区、互斥量和信号量 是实现互斥的实现方式

  > synchronized 关键字修饰，synchronized 关键字经过编译之后，会在同步块的前后分别形成 monitorenter 和 monitorexit 这两个字节码指令，这两个字节码都需要一个 reference 类型的参数来指明要锁定和解锁的对象

### wait 和 sleep 的区别

- wait 是 Object 类中的方法，是非静态方法；sleep 是在当前线程上操作并且方法是定义在 Thread 类中，是静态方法。
- wait 方法必须在同步上下文中调用，比如在同步代码块 或者 同步方法中调用，否则会抛出 IllegalMonitorStateException  异常；sleep 不需要。
- wait 方法会释放其调用的对象锁，如果它持有其他锁，同时释放其他锁； sleep 方法 不会释放任何锁。

### 线程池

通过 ThreadPoolExecutor 类创建线程池，参数

- corePoolSize：线程池中的核心线程数，即空闲状态下仍然保持的最小线程数（除非设置了allowCoreThreadTimeOut 为 true）；

- maximumPoolSize：线程池中允许的最大线程数；

- keepAliveTime：当线程数大于大于核心线程数时，空闲线程等待时间超过该设置时间将时将被终止；

- TimeUnit：时间单位

- BlockingQueue：阻塞队列，用来存储等待执行的任务，以下几种选择

  > ArrayBlockingQueue：基于数组实现的阻塞队列
  >
  > LinkedBlockingQueue：基于 链表实现的 先入先出的 阻塞队列
  >
  > SynchronousQueue：该阻塞队列没有内部容量

- ThreadFactory：线程工厂，主要用来创建线程

- RejectedExecutionHandler：当拒绝处理任务的策略，有以下四种

  >ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。 
  >ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。 
  >ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
  >ThreadPoolExecutor.CallerRunsPolicy：由调用线程处理该任务 

Executors 工具类提供了几种线程池的创建：

- Executors .newFixedThreadPool(int nThreads)：固定线程数

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

- Executors.newCachedThreadPool()：核心线程数为0，最大线程数为无穷大

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


####　ctl

> 线程池用于控制状态的属性，是 Integer 的原子操作类，32位；自身携带两个字段的信息
>
> workerCount: 有效线程数； 最大可存储 2^29-1 个线程，即存储在低 28位中
>
> runState: 运行状态，存储在高位中
>
> > Running: 接收新任务，处理队列中的任务； -1 >> 29
> >
> > shutdown: 不接收新任务，处理队列中的任务；0>>29
> >
> > stop: 不接收新任务，不处理队列中的任务；1>>29
> >
> > tidying（整理）: 所有的任务都终止了，workerCount 为0，所有线程为整理状态，将运行终止的hook 方法；2>>29
> >
> > terminated:  terminated() 方法已经完成；3>>29

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

定义程序中各个变量的访问规则

决定在程序运行过程中什么值可以读取

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











