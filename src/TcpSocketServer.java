import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpSocketServer {

    static final int PORT = 9090;

    public void listen() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa al port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connectat");

                new ServidorThread(clientSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TcpSocketServer().listen();
    }
}
