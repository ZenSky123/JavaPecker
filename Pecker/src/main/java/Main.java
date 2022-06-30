import com.sun.tools.attach.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

public class Main {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Server.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();

        Thread.sleep(1000);

        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : list) {
            if (vmd.displayName().equals("com.github.realsky.aweb.AwebApplication")) {
                System.out.println("enter vm");
                VirtualMachine vm = VirtualMachine.attach(vmd.id());
                vm.loadAgent("/Users/carotwang/IdeaProjects/JavaPecker/Pecker/target/Pecker-1.0-SNAPSHOT.jar");
                vm.detach();
            }
        }
        thread.join();
    }
}
