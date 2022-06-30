import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.Optional;

public class Client {
    public static SocketChannel channel;

    public static void run(String agentArgs, Instrumentation inst) throws IOException, InterruptedException, ClassNotFoundException, NoSuchMethodException {
        Path socketPath = Path.of(System.getProperty("user.home")).resolve(".pecker/pecker.sock");

        UnixDomainSocketAddress socketAddress = UnixDomainSocketAddress.of(socketPath);
        channel = SocketChannel.open(StandardProtocolFamily.UNIX);
        channel.connect(socketAddress);
        send("connected!");

        new Thread(() -> {
            while (true) {
                try {
                    read().ifPresent(message -> {
                        if (message.equals("start")) {
                            inst.addTransformer(new DefineTransformer(), true);
                            for (Class aClass : inst.getAllLoadedClasses()) {
                                if (aClass.getName().equals("com.github.realsky.aweb.HelloController")) {
                                    try {
                                        inst.retransformClasses(aClass);
                                    } catch (UnmodifiableClassException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                            send("suc!");
                        }
                    });
                    Thread.sleep(100);
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public static void send(String msg) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int length = msg.length();
        for (int i = 0; i < length; i += 1024) {
            String substring = msg.substring(i, Math.min(i + 1024, length));
            buffer.clear();
            buffer.put(substring.getBytes());
            buffer.flip();
            while (buffer.hasRemaining()) {
                try {
                    channel.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    return;
                }
            }
        }
    }

    private static Optional<String> read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(buffer);
        if (bytesRead < 0) return Optional.empty();

        byte[] bytes = new byte[bytesRead];
        buffer.flip();
        buffer.get(bytes);
        String message = new String(bytes);
        return Optional.of(message);
    }
}
