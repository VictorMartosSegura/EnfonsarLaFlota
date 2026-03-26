import java.io.*;
import java.net.Socket;

public class ServidorThread extends Thread {

    private final Socket clientSocket;
    private final Tauler tauler;

    public ServidorThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.tauler = new Tauler();
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintStream out = null;
        String clientMessage;
        boolean gameOver = false;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintStream(clientSocket.getOutputStream());

            out.println("Benvingut a Enfonsar la flota");
            out.println("Introdueix una casella (ex: A1, B7, J10)");
            out.flush();

            System.out.println("\nNou tauler per al client: " + clientSocket.getRemoteSocketAddress());
            tauler.mostrarTaulerServidor();

            do {
                clientMessage = in.readLine();

                if (clientMessage != null) {
                    clientMessage = clientMessage.toUpperCase().trim();

                    String response = tauler.processarTirada(clientMessage);

                    System.out.println("Client " + clientSocket.getRemoteSocketAddress() + " -> " + clientMessage);
                    tauler.mostrarTaulerServidor();

                    out.println(response);
                    out.flush();

                    gameOver = response.startsWith("GUANYAT:");
                }

            } while (clientMessage != null && !gameOver);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException ignored) {
            }
            if (out != null) out.close();
            try {
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ignored) {
            }

            System.out.println("Client desconnectat: " + clientSocket.getRemoteSocketAddress());
        }
    }
}