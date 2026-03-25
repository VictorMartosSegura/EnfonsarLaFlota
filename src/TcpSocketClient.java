import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TcpSocketClient {

    public void connect(String address, int port) {

        Socket socket;
        BufferedReader in;
        PrintStream out;
        Scanner sc = new Scanner(System.in);

        try {
            socket = new Socket(InetAddress.getByName(address), port);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());

            System.out.println("Servidor: " + in.readLine());
            System.out.println("Servidor: " + in.readLine());

            String request;
            String serverData;

            while (true) {
                System.out.print("Escriu una casella: ");
                request = sc.nextLine();

                out.println(request);
                out.flush();

                serverData = in.readLine();
                if (serverData == null) {
                    break;
                }

                System.out.println("Resposta: " + serverData);

                if (serverData.equalsIgnoreCase("HAS GUANYAT! TOTS ELS VAIXELLS HAN ESTAT ENFONSATS")) {
                    break;
                }
            }

            socket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TcpSocketClient client = new TcpSocketClient();
        client.connect("localhost", 9090);
    }
}