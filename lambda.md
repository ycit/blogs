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

