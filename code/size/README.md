1. 编写agent 代理类 SizeUtil.java

   ```java
   import java.lang.instrument.Instrumentation;
   
   public class SizeUtil {
   	private static Instrumentation in;
   
       public static void premain(String agentArgs, Instrumentation instr) {
           in = instr;
       }
   
       public static long sizeof(Object obj) {
           return in.getObjectSize(obj);
       }
   }
   ```

2. 编译生成class 文件

   ```cmd
   javac SizeUtil.java
   ```

   

3. 创建MANIFEST.MF文件,最后有个空行

   ```MANIFEST
   Manifest-Version: 1.0
   Premain-Class: SizeUtil
   
   ```

   

4. 生成jar 包

   ```cmd
   jar cvfm sizeUtil.jar META-INF/MANIFEST.MF
   ```

   

5. 创建测试类 SizeTest.java

   ```java
   public class SizeTest{
   	
   	public static void main(String[]args) {
   		System.out.println(SizeUtil.sizeof(new Object()));
   	}
   	
   }
   ```

   

6. 编译 SizeTest

   ```cmd
   javac SizeTest.java
   ```

   

7. 使用代理执行 SizeTest

   ```cmd
   java -javaagent:sizeUtil.jar SizeTest
   ```

   