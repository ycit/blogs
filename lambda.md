## Functional interface

### Predicate\<T>

T →  boolean：输入一个值，返回这个值的断言（boolean 值）

```java
boolean test(T t);
```

### Consumer\<T>

T  →   void：输入一个值，不返回任何值

```java
void accept(T t);
```

### Function\<T,R>

T → R：输入一个值，返回另外一个值

```java
R apply(T t);
```

### Supplier\<T>

void → T：不输入任何值，返回一个值

```java
T get();
```



### Optional

#### 错误使用

1. 直接调用 isPresent 方法 : 和 非空判断无异
2. 直接调用 get 方法：容易导致空指针异常
3. Optional 类型作为类/实例属性
4. Optional 类型作为方法参数

#### 常用正确的方法

1. Optional.of()
2. Optional.ofNullable()
3. Optional.empty()
4. 