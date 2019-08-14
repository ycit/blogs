## Java 类加载机制

### Java类加载过程

> 基于 JDK8，另 ==xx== 为高亮操作，高亮 xx，Typora 支持，而掘金不支持)

类从被加载到虚拟机内存中开始，到卸载出内存为止，它的整个生命周期包括

- ==加载== Loading
- 链接 Linking
  - ==验证== Verification
  - ==准备== Preparation
  - ==解析== Resolution
- ==初始化== Initialization
- ==使用== Using
- ==卸载== Unloading

**加载**、**验证**、**准备**、**初始化**和 **卸载** 这5个阶段的顺序是确定的，类的加载过程必须按照这种顺序按部就班地开始，然后通常互相交叉地混合式进行，而解析阶段则不一定：它在某些情况下可以在初始化阶段之后再开始，这是为了支持Java语言的运行时绑定（也称为动态绑定或晚期绑定）。

#### 加载

加载的过程中主要做了3件事：

- 通过一个类的全限定名来获取定义此类的二进制流
- 将二进制流所代表的存储结构转化为==方法区==的运行时数据结构。
- 在内存中生成一个代表这个类的 java.lang.Class 对象，作为方法区的各种数据访问入口。

对于一个类或者接口

- 如果该类不是数组类，则使用类加载器加载二进制表示。
- 如果该类是数组类，则由Java虚拟机创建，因为数组类不具有外部二进制表示形式。

##### 类加载器种类

有两种类型的类加载器：

- 由 JVM 提供的 ==bootstrap 类加载器==：用于加载 系统变量 sun.boot.class.path  所代表的路径下的 class 文件，顶层父类

- 用户定义的类加载器（java 类库中定义的也包括在内）

  - ==ExtClassLoader==：sun.misc.Launcher 类中定义，用于加载 系统变量 java.class.path 所代表的路径下的 class 文件；父类加载器为 bootstrap
  - ==AppClassLoader==：sun.misc.Launcher  类中定义，用于加载 系统变量 java.ext.dirs 所代表的 路径下 class 文件；父类加载器为 ExtClassLoader
  - 其他，当然你也可以定义其他类型的类加载器

##### 类加载器加载原理

ClassLoader 使用 ==双亲委派模型== 来加载类，每个 ClassLoader 实例都持有父类加载器的引用，虚拟机内置的 bootstrap 类加载器为顶层父类加载器，没有父类加载器，但可以作为其它 ClassLoader 实例的父类加载器。当 ClassLoader 实例需要加载某个类时，它会先委派其父类加载器去加载。这个过程是由上至下依次检查的，首先由最顶层的类加载器Bootstrap ClassLoader试图加载，如果没加载到，则把任务转交给Extension ClassLoader试图加载，如果也没加载到，则转交给App ClassLoader 进行加载，如果它也没有加载得到的话，则返回给委托的发起者，由它到指定的文件系统或网络等URL中加载该类。如果它们都没有加载到这个类时，则抛出ClassNotFoundException异常。否则将这个找到的类生成一个类的定义，并将它加载到内存当中，最后返回这个类在内存中的Class实例对象。

双亲委派机制的优点是可以避免类的重复加载，当父类加载了子类就没必要再加载。另外能够保证虚拟机的安全，防止内部实现类被自定义的类替换。

那么JVM在搜索类的时候，如何判断两个 class 是否相同呢？答案是不仅全类名要相同 ，而且还要由同一个类加载器实例加载。

#### 验证

主要确保类或接口的二进制表示在结构上是正确的。

#### 准备

准备工作包括为类或接口创建静态字段，并将这些字段初始化为其默认值，这里 不需要执行任何 java 代码。

例如，对于类或接口中的如下静态字段

```java
private static int num = 666;
```

在准备阶段会为 num 设置默认值 0；在后面的初始化阶段才会给 num 赋值 666；

> 特殊情况：对于同时被 static  和 final 修饰的 字段，准备阶段就会赋值。

#### 解析

解析是将运行常量池中的符号引用 动态确定为具体值的过程。解析动作主要针对类或接口、字段、类方法、接口方法、方法类型、方法句柄和调用点限定符7类符号引用进行。

#### 初始化

初始化阶段是初始化类变量和其他资源，或者说是执行类构造器\<clinit>()方法的过程.

\<clinit>()方法是由编译器自动收集类中的所有==类变量==(static 修饰的变量)的赋值动作和静态语句块static{}中的语句合并产生的，编译器收集的顺序是由语句在源文件中出现的顺序所决定的，静态语句块只能访问到定义在静态语句块之前的变量，定义在它之后的变量，在前面的静态语句块可以赋值，但是不能访问。

例如：非法向前引用变量示例

```java
static {
    i = 2;
    System.out.println(i); //illegal forward reference
}
static int i = 4;
```
\<clinit>()方法与类的构造函数(或者说实例构造器\<init>()方法)不同，它不需要显示地调用父类构造器，虚拟机会保证在子类的\<clinit>()方法执行之前，父类的\<clinit>()方法已经执行完毕。因此在虚拟机中第一个被执行的\<clinit>()方法的类肯定是 java.lang.Object。接口与类不同的是，执行接口的\<clinit>()方法不需要先执行父接口的\<clinit>()方法，只有当父接口中定义的变量使用时，父接口才会初始化。另外，接口的实现类在初始化时也一样不会执行接口的\<clinit>()方法。

\<clinit>()方法是类构造器，在类初始化的时候执行，用于 类中的静态代码块 和 静态字段 初始化的方法，只会执行一次。

\<init>()方法是 类实例的构造器，在对象的初始化阶段执行，用于非静态字段，非静态代码块，构造函数的初始化，可以执行多次。

类或者接口只能由于以下原因初始化：

1. 使用 new 创建新对象，如果引用的类尚未被初始化，则初始化该类；或者从类中获取 静态字段、设置静态字段、执行类中的静态方法时，如果还没有被初始化，则 ==声明该字段或者方法的类或者接口被初始化==
2. 第一次调用 java.lang.invoke.method handle实例，调用了静态方法，或者 new 的方法的句柄。
3. 使用 java.lang.reflect 包的方法对类进行反射调用的时候，如果类没有进行过初始化，则需要先触发其初始化
4. 当初始化一个类的时候，如果发现其父类还没有进行过初始化，则需要先触发其父类的初始化
5. 当初始化一个类，而该类直接或者间接 实现了 一个 不含有抽象方法，和静态方法的接口，则需要初始化该接口。
6. 当虚拟机启动时，用户需要指定一个要执行的主类（包含main()方法的那个类），虚拟机会先初始化这个主类。

### 牛刀小试1

执行如下代码，输出如何：

```java
public class SSClass
{
    static
    {
        System.out.println("SSClass");
    }
}    
public class SuperClass extends SSClass
{
    static
    {
        System.out.println("SuperClass init!");
    }

    public static int value = 123;

    public SuperClass()
    {
        System.out.println("init SuperClass");
    }
}
public class SubClass extends SuperClass
{
    static 
    {
        System.out.println("SubClass init");
    }

    static int a;

    public SubClass()
    {
        System.out.println("init SubClass");
    }
}
public class NotInitialization
{
    public static void main(String[] args)
    {
        System.out.println(SubClass.value);
    }
}

```

输出：

```result
SSClass
SuperClass init!
123
```

解析：

上面提到了这样一句话：

==从类中获取 静态字段、设置静态字段、执行类中的静态方法时，如果还没有被初始化，则 声明该字段或者方法的类或者接口被初始化==

所以 SubClass 类并不会被初始化，所以也就不会执行其 静态代码块；

### 牛刀小试2

```java
public class StaticTest {

    public static void main(String[] args) {
        staticFunction();
    }

    static StaticTest st = new StaticTest();

    static {
        System.out.println("1");
    }

    {
        System.out.println("2");
    }

    StaticTest() {
        System.out.println("3");
        System.out.println("a=" + a + ",b=" + b);
    }

    public static void staticFunction() {
        System.out.println("4");
    }

    int a = 110;
    static int b = 112;
}

```

问题：执行上面的程序，输出结果是什么？

答案：执行以上代码的输出结果：

```result
2
3
a=110,b=0
1
4
```

解析：

- 执行 main 方法，会导致主类 StaticTest 初始化，由于还未加载，先执行加载，主要分析 准备阶段 和 初始化阶段 的 赋值操作。

- 准备阶段 : 为静态字段赋初始值，st 设为 null ，b 设为 0；
- 初始化阶段：执行 Java 代码的类构造器 \<clinit>()方法，分别按顺序执行如下代码：
  - static StaticTest st = new StaticTest()；new 会 导致 StaticTest 类执行类初始化，执行 \<init>()方法，对象的初始化是先初始化成员变量 和 代码块，再载执行构造方法；
    - int a = 110;
    - System.out.println("2");
    - System.out.println("3");
    - System.out.println("a=" + a + ",b=" + b);
  - static 静态代码块，System.out.println("1");
  - static int b = 112;
- 调用 staticFunction() 方法，System.out.println("4");

稍微修改一下代码，去除 以下代码，或许就变得正常多了，

```java
static StaticTest st = new StaticTest();
```

输出：

```result
1
4
```

减少了 类实例化的步骤。

父类和子类的初始化顺序可以简单用以下几句话概括：

- 父类的静态变量赋值
- 自身的静态变量赋值
- 父类成员变量赋值和父类块赋值
- 父类构造函数赋值
- 自身成员变量赋值和自身块赋值
- 自身构造函数赋值


参考：

> 深入理解Java虚拟机