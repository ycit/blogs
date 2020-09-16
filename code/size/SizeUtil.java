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