import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TcpSocketClient {

    private static final int MIDA = 10;
    private final char[][] taulerClient = new char[MIDA][MIDA];

    public TcpSocketClient() {
        inicialitzarTaulerClient();
    }

    private void inicialitzarTaulerClient() {
        for (int i = 0; i < MIDA; i++) {
            for (int j = 0; j < MIDA; j++) {
                taulerClient[i][j] = '#';
            }
        }
    }

    public void connect(String address, int port) {

        Socket socket;
        BufferedReader in;
        PrintStream out;
        Scanner scanner = new Scanner(System.in);

        try {
            socket = new Socket(InetAddress.getByName(address), port);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());

            System.out.println("Servidor: " + in.readLine());
            System.out.println("Servidor: " + in.readLine());

            mostrarTaulerClient();

            while (true) {
                System.out.print("Escriu una casella: ");
                String request = scanner.nextLine().toUpperCase().trim();

                out.println(request);
                out.flush();

                String serverData = in.readLine();
                if (serverData == null) {
                    break;
                }

                processarRespostaServidor(request, serverData);

                if ("GUANYAT".equalsIgnoreCase(serverData)) {
                    System.out.println("Has guanyat!");
                    break;
                }

                if ("ENFONSAT".equalsIgnoreCase(serverData)) {
                    String coordenades = in.readLine(); // ex: A1,A2
                    marcarEnfonsat(coordenades);
                    System.out.println("Resposta: ENFONSAT");
                } else if ("GUANYAT".equalsIgnoreCase(serverData)) {
                    System.out.println("Has guanyat!");
                    break;
                } else {
                    System.out.println("Resposta: " + serverData);
                }

                mostrarTaulerClient();

                // si después de ENFONSAT el servidor manda GUANYAT
                if ("ENFONSAT".equalsIgnoreCase(serverData)) {
                    String possibleWin = in.readLine();
                    if (possibleWin != null && "GUANYAT".equalsIgnoreCase(possibleWin)) {
                        System.out.println("Has guanyat!");
                        break;
                    }
                }
            }

            socket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void processarRespostaServidor(String request, String resposta) {
        if (!formatCorrecte(request)) {
            return;
        }

        int fila = request.charAt(0) - 'A';
        int columna = Integer.parseInt(request.substring(1)) - 1;

        switch (resposta) {
            case "AIGUA":
                taulerClient[fila][columna] = '~';
                break;
            case "TOCAT":
                taulerClient[fila][columna] = '1';
                break;
            case "CASELLA JA UTILITZADA":
            case "FORMAT INCORRECTE. Exemple: A1 o J10":
            case "FORA DE TAULER":
                break;
            default:
                break;
        }
    }

    private void marcarEnfonsat(String coordenades) {
        String[] parts = coordenades.split(",");
        for (String coord : parts) {
            coord = coord.trim().toUpperCase();
            if (formatCorrecte(coord)) {
                int fila = coord.charAt(0) - 'A';
                int columna = Integer.parseInt(coord.substring(1)) - 1;
                taulerClient[fila][columna] = 'X';
            }
        }
    }

    private boolean formatCorrecte(String casella) {
        if (casella == null || casella.length() < 2 || casella.length() > 3) {
            return false;
        }

        char fila = casella.charAt(0);
        if (fila < 'A' || fila > 'J') {
            return false;
        }

        try {
            int columna = Integer.parseInt(casella.substring(1));
            return columna >= 1 && columna <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void mostrarTaulerClient() {
        System.out.println("\nTAULER CLIENT");
        System.out.print("     ");
        for (int j = 1; j <= MIDA; j++) {
            System.out.printf("%-4d", j);
        }
        System.out.println();

        for (int i = 0; i < MIDA; i++) {
            System.out.print("   +");
            for (int j = 0; j < MIDA; j++) {
                System.out.print("---+");
            }
            System.out.println();

            System.out.printf(" %c |", (char) ('A' + i));
            for (int j = 0; j < MIDA; j++) {
                System.out.print(" " + taulerClient[i][j] + " |");
            }
            System.out.println();
        }

        System.out.print("   +");
        for (int j = 0; j < MIDA; j++) {
            System.out.print("---+");
        }
        System.out.println("\n");
    }

    public static void main(String[] args) {
        TcpSocketClient client = new TcpSocketClient();
        client.connect("localhost", 9090);
    }
}