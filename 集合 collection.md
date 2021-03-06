

# Collection



## List

> 有序的集合

- ArrayList

  > 基于数组的实现，非线程安全的。有角标，查询速度快，可以包含 null 值，插入有序
  >
  > 默认容量：10
  >
  > Vector 是线程安全的，其他和 ArrayList 一致。 

- LinkedList

  > 基于双向链接列表的实现，非线程安全。新增，删除速度比较快，不需要重排序，可以包含null 值，插入有序。

## Set

> 不包含重复元素的集合

- HashSet

  > 基于 哈希表的实现，内部实际上是 HashMap 实现，value 为 static final 修饰的 常量 Object 实例。因为HashMap在 remove 时会返回 value ,然后需要通过比较 value 来判断是否移除成功。插入无序，按 key 的 哈希码排序
  >
  > 默认容量：HashMap 的 16

- LinkedHashSet

  > 基于哈希表和链接列表的实现，插入有序

# Map



## HashMap

### HashMap简介
HashMap 是基于**Map**接口的**哈希表**的实现，内部是数组+链表+红黑树的数据结构，当桶的长度 \> 8 时转为红黑树，当桶的长度 \< 7 时转为链表。HashMap 支持 **null**的键和**null**的值。是线程不安全的。不保证插入顺序。
Hashtable 也是基于**Map**接口的哈希表的实现。但是不支持**null**的键和**null**的值。是线程安全的。其他基本和 HashMap 相同。

### HashMap数组的扩容

- 扩容时机

  > 当第一次放入元素 或者  HashMap 中元素数量 大于 threshold 时，进行扩容。扩容大小可能为默认初始化容量(16，默认容量为0 并且 threshold  为 0)，也可能为原来容量的两倍（默认容量大于0），也可能为 threshold 大小（默认容量为0 且 threshold 大于 0）
  >
  > threshold 在 HashMap 指定初始容量 初始化时 会 扩容为 初始化容量的最小 2次幂，之后会在链表扩容的时候扩容；若未指定初始化容量，则默认为0，则在链表扩容的时候扩容为 默认容量*加载因子（16\*0.75）

- 扩容完成后需要 重新计算每个元素在数组中的位置

### HashMap 索引计算

哈希表 散列法的思想是 在桶数组中分配 entry（键值对）。给定一个键 key，该算法计算出一个索引，该索引代表了 entry 的地址。索引通常有两步完成：计算 hash；hash 和数组大小取模运算 得到索引值。

```hash
hash = hashfunc(key)
index = hash % array_size
```



HashMap 中 hash 计算 ：键对应的 hashCode 异或 hashCode 的高 16位

这种做法相对效率更高，同时保留了高位和地位的特征，减少碰撞几率

```java
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

索引计算：

因为 table.length 为 2的 n 次幂，二进制表示只有一个1，

减一之后，1位变为0，1后位全部变为1，例如:

| eg   | 原始二进制                        | -1后二进制                        |
| ---- | --------------------------------- | --------------------------------- |
| 4    | 0000000000000000 0000000000000100 | 0000000000000000 0000000000000011 |
| 8    | 0000000000000000 0000000000001000 | 0000000000000000 0000000000000111 |

再逻辑与 hash 值后获取的值肯定在 [0,table.length - 1] 之间，即 获取到了索引

```java
(table.length - 1) & hash
```

### HashMap 插入元素

1. 若第一次插入元素，先扩容；否则继续执行
2. 计算索引，判断 key 在该索引处是否有值；没有值则直接插入索引处
3. 若有值，判断是重复 还是发生碰撞；若重复则直接覆盖
4. 若发生碰撞，判断是红黑树 还是 链表；若为红黑树，直接插入
5. 若为链接列表，循环找到链表的尾部，若在过程中找到相同的key,value则退出，否则将值插入到链表尾部，然后判断链表长度是否超过 8，若超过 8 则进行树化桶
6. 判断数组是否需要扩容

### HashMap 移除元素



