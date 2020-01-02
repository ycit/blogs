## 深入剖析 kubernetes

### 从容器进程说开去

> 容器是一种沙盒技术，是运行在宿主机上的一种特殊的进程。
>
> 容器实现原理：Docker 容器在创建容器进程时，指定了这个进程所需要启用的一组 Namespace参数。这样，容器就只能'看到'当前 Namespace所限定的资源、文件、设备、状态，或者配置。Namespace 包括: PID Namespace，Mount Namespace，UTS Namespace，IPC Namespace，Network Namespace，User Namespace.

### 隔离与限制

> 容器的隔离：namespace
>
> 容器的限制：linux cgroups，linux controll groups，限制一个进程组能够使用的资源上限，包括 CPU，内存，磁盘，网络带宽等；对进程优先级设置、审计，以及将进程挂起和恢复

### 深入理解容器镜像

linux 容器文件系统的实现方式：容器镜像，也叫 rootfs，是一个操作系统所包含的文件、配置、目录，并不包括操作系统内核。

### 重新认识 docker

- 制作镜像

  > Dockerfile：使用一些标准原语，描述我们所要构建的 Docker 镜像。并且这些原语都是按顺序处理的，每个原语执行后，都会生成一个对应的镜像层
  >
  > FROM原语：指定基础镜像，
  >
  > WORKDIR原语：在这一句之后，Dockerfile 后面的操作都以这一句话指定的目录作为当前目录   WORKDIR  /app   将工作目录切换为 /app
  >
  > ADD原语：将当前目录下的所有内容复制到指定目录下    ADD .   /app
  >
  > RUN原语：执行脚本
  >
  > EXPOSE原语：暴露给外界的端口号   EXPOSE 80
  >
  > ENV原语：设置环境变量        ENV NAME world
  >
  > CMD原语：设置容器进程        CMD["python","app.py"]   等价于 docker run python app.py
  >
  > ENTRYPOINT原语：默认docker会提供一个隐含的 ENTRYPOINT，即/bin/sh -c ,CMD 的内容就是ENTRYPOINT的参数

- 命令

  > docker build -t tag_name .              -t  给这个镜像加一个 Tag
  >
  > docker image ls     查看本地镜像
  >
  > docker run -p 4000:80 tag_name   启动容器，-p 把容器内的80端口映射在宿主机的4000端口上
  >
  > docker ps     查看运行中的容器
  >
  > docker inspect --format {{.State.Pid}} container_id
  >
  > ls -l /proc/pid/ns  查看namespace
  >
  > docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]
  >
  > docker push name
  >
  > docker commit  container_id repository
  >
  > docker exec -it container_id /bin/sh    进入容器内部
  >
  > 把宿主机目录挂载进容器的 /test 目录当中: volume
  >
  > docker run -v /test ...
  >
  > docker run -v /home:/test ...

```Dockerfile

# 使用官方提供的Python开发镜像作为基础镜像
FROM python:2.7-slim

# 将工作目录切换为/app
WORKDIR /app

# 将当前目录下的所有内容复制到/app下
ADD . /app

# 使用pip命令安装这个应用所需要的依赖
RUN pip install --trusted-host pypi.python.org -r requirements.txt

# 允许外界访问容器的80端口
EXPOSE 80

# 设置环境变量
ENV NAME World

# 设置容器进程为：python app.py，即：这个Python应用的启动命令
CMD ["python", "app.py"]
```

### 谈谈kubernetes 的本质

#### kubernetes 架构

![kubernetes全局架构](D:\study\kubernetes\kubernetes 全局架构.png)

- master (控制节点)

> kube-apiserver：	负责api服务
>
> kube-scheduler：负责调度
>
> kube-controller-manager：负责容器编排
>
> Etcd：保存持久化数据

- node (计算节点)

> kubelet：
>
> - 负责和容器运行时(如 Docker项目)打交道，交互所依赖的是 CRI （Container Runtime Interface）的远程调用接口。
>
> 具体的容器运行时，比如 Docker 项目，则一般通过 OCI 这个容器运行时规范同底层的 Linux 操作系统进行交互，即：把 CRI 请求翻译成对 Linux 操作系统的调用（操作 Linux Namespace 和 Cgroups 等）。
>
> - 通过 gRPC 协议同一个叫做 Device Plugin 的插件交互。
>
> - 调用网络插件和存储插件为容器配置网络和持久化存储
>
> 这两个插件与 kubelet 进行交互的接口，分别是 CNI（Container Networking Interface）和 CSI（Container Storage Interface）。

Pod 是 kubernetes 最基础的一个对象，可以给 Pod 绑定一个 Service 服务。Service 服务的作用是 作为 Pod 的代理入口，从而代替 Pod 对外暴露一个固定的网络地址。

核心功能图：

> 容器间的紧密协作→ Pod
>
> 一次启动多个应用的实例→ 需要 Deployment 这个 Pod 的多实例管理器
>
> 需要通过一个固定的 IP 地址和端口负载均衡的方式访问→ service
>
> 两个 Pod 访问关系，并且需要加上授权信息 → Secret
>
> 应用运行的形态 → Job：一次性运行的 Pod 、DaemonSet：每个宿主机上必须有且只有一个副本的守护进程服务、CronJob：定时任务

![](D:\study\kubernetes\kubernetes核心功能全景图.png)

推崇的使用方法：声明式 API

- 通过一个编排对象，比如 Pod 、Job、CronJob 来描述你试图管理的应用
- 然后，再为它定义一些服务对象，比如 Service、Secret、Horizontal Pod AutoScaler（自动水平扩展器）

```
serialNo  serialUser  auditStatus  serialBasis  auditUser  serialDescription  serialType
```