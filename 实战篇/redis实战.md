## Redis 安装

版本：redis6.0.8

gcc 升级到5以上

```shell
yum -y install centos-release-scl
yum -y install devtoolset-9-gcc devtoolset-9-gcc-c++ devtoolset-9-binutils
scl enable devtoolset-9 bash
echo "source /opt/rh/devtoolset-9/enable" >>/etc/profile
```

jemalloc 报错

```shell
make MALLOC=libc
```

启动：./redis-server

## HA sentinel

### 主从配置

> redis.conf

1. slave redis 实例配置 master redis ip

   ```
   #eg replicaof 192.168.31.221 6379
   slaveof(replicaof) <master_ip> <master_port>
   ```

2. 其他配置

   ```conf
   #开启验证
   masterauth <password>
   #开启后台运行
   daemonize yes   
   ```

3. bind 127.0.0.1 修改为 0.0.0.0 或者 ip

### sentinel 配置

> sentinel.conf

1. 修改监听的 ip

   ```
   # sentinel monitor mymaster 192.168.31.221 6379 2
   sentinel monitor <master_name> <master_ip> <master_port> <replicas_num>
   ```

### 启动

1. 启动 redis 服务

   ```
   ./src/redis-server redis.conf
   ```

2. 启动 sentinel 服务

   ```shell
   ./src/redis-sentinel sentinel.conf
   #or
   ./src/redis-server sentinel.conf --sentinel
   ```

   

   