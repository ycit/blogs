# 缓存

## 缓存问题

### 缓存穿透

- What

  查询缓存和数据库中都没有的数据

  查询数据库中一定不存在的数据，这样每次都会绕过缓存直接打到数据库

- How

  - 缓存空值，但是需要设置过期时间。

    > 存在问题：内存中会存在大量不存在的key的数据
    
  - 布隆过滤器 BloomFilter

    > 本质上是一种数据结构，概率型数据结构；
  >
    > 组成：由一个**很长的二进制向量**（bit 数组）和**一系列随机映射函数（哈希函数）**，用于把元素的hash 值算得比较均匀。
    >
    > 原理：向布隆过滤器中添加元素 时，会使用多个不同的 hash 函数生成多个哈希值，接着将 哈希值 对 bit 数组的长度 取余，将该值在 位图数组中对应位置置为1。判断元素是否存在时，同样会使用这些 hash 函数生成 哈希值，并对 bit 数组的长度 取余，判断获得的值 在位图数组中的位置是否全部为1，如果全部为1，那么该元素有可能存在过滤器中，如果有一个为0，则可以断定该 元素肯定不存在。
    >
    > 在缓存之前加一层 BloomFilter，记录已有缓存的key，查询的时候先去 BloomFilter 查询，如果不存在就直接返回，存在再接着查询缓存，查询 db.
    >
    > 优点：在时间 和 空间上都有很大优势，没有存储元素本身，存储空间 和插入、查询时间都是 常数时间；
    >
    > 缺点：误算率，随着存入元素数量增加，误算率也会随之增加；位图数组的长度会直接影响误报率，越长，误报率越小；hash函数越多 则数组中为1的速度越快，且效率越低；hash 函数越少，误报率也会越高；不支持删除操作；
    >
    > 补救办法：建立一个小的白名单，存储那些可能被误判的元素。
    >
    > 应用场景：实时消息推送的去重，网页URL的去重，垃圾邮件的判别，集合重复元素的判别

### 缓存击穿

- What

  查询缓存中没有的数据，数据库中有的数据（一般是缓存时间到期）。这时由于并发用户特别多，同时读取缓存读不到，又同时去数据库读，导致数据库压力瞬间增大。

- How

  - 热点数据永不过期
  - 添加互斥锁：第一个查询数据的请求使用互斥锁来锁住，其他线程拿不到锁就一直等待，直到第一个线程查询到数据，设置了缓存，那么后面的线层就会直接走缓存。

### 缓存雪崩

- What

  指缓存中数据大批量到过期时间，而查询数据量巨大，引起数据库压力过大甚至 down 机。和缓存击穿不同的是，缓存击穿指并发查同一条数据，缓存雪崩是指不同数据都过期了

- How

  - 缓存数据的过期时间设置随机
  - 设置多级缓存

## 热点问题

## redis

redis 是一个开源的，保存在内存中，亦可持久化的 key-value 类型数据结构，可用作数据库，缓存，消息队列。

redis 内置了  副本、 Lua 脚本、 LRU 淘汰策略、事务、不同级别的磁盘持久化，可通过 redis sentinel 和 Redis cluster自动分区来保证高可用。

### 基本数据类型

> - String 
> - Hash：value 为 hashmap ,常用于 用户缓存，方便操作
> - List
> - Set
> - Sorted set

### 集群模式

1. 哨兵模式

   > 哨兵是部署的一个单独的进程，通过监控 Redis 实例，保证高可用； 同时哨兵也需要部署多个，来保证高可用。

2. Cluster 集群

   > 

### 单线程为什么这么快

> - 纯内存操作
> - 单线程操作，避免频繁的上下文切换
> - 采用非阻塞 I/O 多路复用机制

### 过期策略以及内存淘汰机制

 `maxmemory-policy` 设置

- **noeviction**: 不删除策略, 达到最大内存限制时, 如果需要更多内存, 直接返回错误信息。大多数写命令都会导致占用更多的内存(有极少数会例外。
- **allkeys-lru:**所有key通用; 优先删除最近最少使用(less recently used ,LRU) 的 key。
- **allkeys-random:**所有key通用; 随机删除一部分 key。
- **volatile-random**: 只限于设置了 **expire** 的部分; 随机删除一部分 key。
- **volatile-ttl**: 只限于设置了 **expire** 的部分; 优先删除剩余时间(time to live,TTL) 短的key。
- **volatile-lru:**只限于设置了 **expire** 的部分; 优先删除最近最少使用(less recently used ,LRU) 的 key。

### 分布式锁

#### 锁特点

- 互斥性
- 可重入性
- 锁超时 ：防止死锁
- 高性能和高可用

#### 实现方式

- 基于数据库：

  > 实现：基于唯一性约束实现； 乐观锁实现
  >
  > 缺点：无锁超时 ， 非重入

- 基于redis

- 基于zookeeper

  > 使用临时顺序节点实现：在创建节点时，Zookeeper根据创建的时间顺序给该节点名称进行编号；当创建节点的客户端与zookeeper断开连接后，临时节点会被删除。

#### redis 实现

##### 使用 set 命令：

   设置 和 过期 为原子操作

```redis
SET key value [EX seconds|PX milliseconds] [NX|XX] [KEEPTTL]
```

参数

- `EX` *seconds* -- 设置过期时间，单位秒 ; since 2.6.12
- `PX` *milliseconds* --设置过期时间，毫秒 ; since 2.6.12
- `NX` -- 当key 不存在时才设置成功; since 2.6.12
- `XX` -- 当key存在时才设置成功; since 2.6.12
- `KEEPTTL` -- Retain the time to live associated with the key；since 6.0

返回值

- 如果执行成功，返回 ok
- 如果实行失败， 返回 Null

eg：设置 一个 key 为 anotherkey，  value 为  will expire in a minute， 过期时间 60s

获取锁：set

```redis
SET anotherkey "will expire in a minute" EX 60
```

释放锁：lua 脚本，需要验证 value

```lua
if redis.call("get",KEYS[1]) == ARGV[1]
then
    return redis.call("del",KEYS[1])
else
    return 0
end
```

```JSON
if redis.call("get",KEYS[1]) == ARGV[1] then return redis.call("del",KEYS[1]) else return 0 end
```

代码实现：

```java
    public boolean lock(Jedis jedis,String key, String value, int timeout, TimeUnit timeUnit) {
        long seconds = timeUnit.toSeconds(timeout);
        return "OK".equals(jedis.set(key, value, "NX", "EX", seconds));
    }

    public boolean unlock(Jedis jedis,String key,String value) {
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                "return redis.call('del',KEYS[1]) else return 0 end";
        return jedis.eval(luaScript, Collections.singletonList(key), Collections.singletonList(value)).equals(1L);
    }
```

##### 使用 lua 脚本 ：

   set  和 expire 两条语句，使用 lua 脚本保证原子性

代码实现：

```java
public boolean tryLock_with_lua(String key, String UniqueId, int seconds) {
    String lua_scripts = "if redis.call('setnx',KEYS[1],ARGV[1]) == 1 then" +
            "redis.call('expire',KEYS[1],ARGV[2]) return 1 else return 0 end";
    Object result = jedis.eval(lua_scripts, ImmutableList.of(key), ImmutableList.of(UniqueId, String.valueOf(seconds)));
    //判断是否成功
    return result.equals(1L);
}

    public boolean unlock(Jedis jedis,String key,String value) {
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                "return redis.call('del',KEYS[1]) else return 0 end";
        return jedis.eval(luaScript, Collections.singletonList(key), Collections.singletonList(value)).equals(1L);
    }
```

```java
private static final RedisScript<Boolean> newSetIfAbsentScript = new DefaultRedisScript("if 1 == redis.call('setnx',KEYS[1],ARGV[1]) then redis.call('expire',KEYS[1],ARGV[2]) return 1; else return 0; end;", Boolean.class);

    private boolean setIfAbsent(String key, String value, Long seconds) {
        List<String> keys = new ArrayList();
        keys.add(key);
        Object[] args = new Object[]{value, seconds.toString()};
        return (Boolean)this.redisTemplate.execute(newSetIfAbsentScript, keys, args);
    }

    public boolean lock(String key, String value, long timeout) {
        return this.setIfAbsent(key, value, timeout);
    }
```



潜在问题：

实际上在Redis集群的时候也会出现问题，`比如说A客户端在Redis的master节点上拿到了锁，但是这个加锁的key还没有同步到slave节点，master故障，发生故障转移，一个slave节点升级为master节点，B客户端也可以获取同个key的锁，但客户端A也已经拿到锁了，这就导致多个客户端都拿到锁`。

##### Redlock算法 与 Redisson 实现

### 持久化方式

1. RDB 持久化：redis database

   > RDB 是一个非常紧凑（compact）的文件，它保存了 Redis 在某个时间点上的数据集
   >
   > 特点：
   >
   > RDB模式定期将内存中的数据持久化.如果用户允许丢失少量的数据则首选RDB模式,因为RDB模式定期为内存做快照,该方式的备份的速度很快.
   >
   > save 900 1 在15分钟内,如果用户执行了一次set操作则持久化一次
   > save 300 10 在5分钟内,如果用户执行了10次set操作则持久化一次
   > save 60 10000 在1分钟内,如果用户执行了10000set操作则持久化一次

2. AOF 持久化：append of file

   > 把写操作指令，持续的写到一个类似日志文件里(appendonly.aof)
   >
   > #appendfsync always 每次操作都会备份
   > #appendfsync no 关闭AOF模式
   > #appendfsync everysec 每秒备份一次
   >
   > 特点：
   >
   > 1.可以实时数据备份，安全性更好
   > 2.持久化速度较RDB模式慢
   > 3.AOF持久化文件的体积会很大
   > 4.恢复数据时会将日志中记录的所有操作都执行一遍，因此速度会很慢
   > 5.持久化文件明文保存，没有加密

