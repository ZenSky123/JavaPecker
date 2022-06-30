import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Server {
    public static SocketChannel channel;

    public static void run() throws IOException, InterruptedException {
        Path socketPath = Path
                .of(System.getProperty("user.home"))
                .resolve(".pecker/pecker.sock");

        Files.deleteIfExists(socketPath);
        UnixDomainSocketAddress socketAddress = UnixDomainSocketAddress.of(socketPath);
        ServerSocketChannel serverChannel = ServerSocketChannel
                .open(StandardProtocolFamily.UNIX);
        serverChannel.bind(socketAddress);
        channel = serverChannel.accept();
        int a = 0;

        while (true) {
            read().ifPresent(message -> System.out.printf("[Client message] %s\n", message));
            if (a == 0) {
                Thread.sleep(5000);
                send("start");
                a = 1;
            }
        }
    }

    private static Optional<String> read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(buffer);
        if (bytesRead < 0)
            return Optional.empty();

        byte[] bytes = new byte[bytesRead];
        buffer.flip();
        buffer.get(bytes);
        String message = new String(bytes);
        return Optional.of(message);
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
}
