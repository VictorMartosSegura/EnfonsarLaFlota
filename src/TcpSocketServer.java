import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpSocketServer {

    static final int PORT = 9090;
    private boolean end = false;

    public void listen() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor multi-client en marxa al port " + PORT);

            while (!end) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());

                new ServidorThread(clientSocket).start();
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TcpSocketServer().listen();
    }
}
