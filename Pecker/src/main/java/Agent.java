import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;

public class Agent {
    public static void run(String agentArgs, Instrumentation inst) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, UnmodifiableClassException, InterruptedException {
        Client.run(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException, InterruptedException, IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        run(agentArgs, inst);
    }
}
