### JVM 排查

#### top

实时动态查看系统整体运行情况

热键

```
h：显示帮助画面，给出一些简短的命令总结说明；
k：终止一个进程；
i：忽略闲置和僵死进程，这是一个开关式命令；
q：退出程序；
r：重新安排一个进程的优先级别；
S：切换到累计模式；
s：改变两次刷新之间的延迟时间（单位为s），如果有小数，就换算成ms。输入0值则系统将不断刷新，默认值是5s；
f或者F：从当前显示中添加或者删除项目；
o或者O：改变显示项目的顺序；
l：切换显示平均负载和启动时间信息；
m：切换显示内存信息；
t：切换显示进程和CPU状态信息；
c：切换显示命令名称和完整命令行；
M：根据驻留内存大小进行排序；
P：根据CPU使用百分比大小进行排序；
T：根据时间/累计时间进行排序；
w：将当前设置写入~/.toprc文件中。
```

样例：

```cmd
top - 10:26:12 up 49 days,  9:09,  0 users,  load average: 9.37, 11.66, 11.74
Tasks:  17 total,   1 running,  16 sleeping,   0 stopped,   0 zombie
%Cpu(s): 29.8 us,  1.3 sy,  0.0 ni, 68.1 id,  0.0 wa,  0.0 hi,  0.8 si,  0.0 st
KiB Mem : 13192864+total,  5952680 free, 54638460 used, 71337496 buff/cache
KiB Swap:        0 total,        0 free,        0 used. 73490864 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND                                                                                                                    
   17 root      20   0  9.827g 6.849g  19636 S 388.7  5.4  33876:46 java                                                                                                                       
    1 root      20   0   18212   4448   1048 S   0.0  0.0   0:00.68 sh   
```

说明：

```
Cpu(s): 29.8% us[用户空间占用CPU百分比],
1.3%sy[内核空间占用CPU百分比],
0.0%ni[用户进程空间内改变过优先级的进程占用CPU百分比],
0.2%id[空闲CPU百分比], 0.0%wa[等待输入输出的CPU时间百分比],
Mem: 4147888k total[物理内存总量],
2493092k used[使用的物理内存总量],
1654796k free[空闲内存总量],
158188k buffers[用作内核缓存的内存量]
Swap:  0kiB total[交换区总量],
0k used[使用的交换区总量],
0 free[空闲交换区总量],
2013180k cached[缓冲的交换区总量],
```



#### 查看pid

- jps -l 
- ps -ef|grep java
- ps -aux|grep java          

#### 查看pid 状态

- cat  /proc/{pid}/status    
- ps p {pid}-L -o pcpu,pmem,pid,tid,time,tname,cmd             查看进程的线程使用情况
- ps p {pid} -L -o pcpu,pmem,pid,tid,time,tname,cmd |wc -l              查看某个进程下有多少个线程
- printf "%x\n" {tid}   将 线程id (tid) 转换为 16 进制
- jstack -l {pid} >> filename     导出线程栈

####  java 堆转储文件jmap

- jmap -dump:format=b,file=\<fileName> \<pid>    

  eg：jmap -dump:format=b,file=11.dump 11   

#### 查看线程锁持有情况jstack

- jstack -l \<pid> >> \<fileName>    


#### jstat 命令

- jstat -gcutil \<pid> 1000 5         每隔1000ms 展示 gc，执行5次

  java 8 eg：

  ```cmd
    S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT   
    9.70   0.00  52.50  29.59  94.80  91.43  52122 4023.240   146   26.953 4050.193
    9.70   0.00  61.26  29.59  94.80  91.43  52122 4023.240   146   26.953 4050.193
  ```
  
  **参数说明**
  
  S0、S1：新生代的两个 Survivor 区；表示分别使用了 9.7%  和 0 的空间
  
  E：新生代的 Eden 区；表示占用了 52.5% 的空间
  
  O：老年代 old 区；表示占用了 29.59% 的空间
  
M：元空间 Metaspace；堆外内存，表示占用了 94.8% 的空间
  
CCS：压缩比例占用 
  
YGC：yong gc ,新生代垃圾收集，程序运行以来共 发生 Minor GC 52122 次
  
YGCT：Minor GC 总耗时 4023.240 秒
  
FGC：Full GC  146 次
  
FGCT：Full GC 总时间 26.953 秒
  
GCT：GC 总耗时 4050.193 秒

#### arthas

1. dashbord
2. watch 包.类 方法 [params,returnObj]   观测某个方法的参数 和 返回对象信息
3. thread
4. jvm   查看当前jvm 信息
5. sysprop    系统属性