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

    // ANSI colors
    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String WHITE = "\u001B[37m";
    private static final String CYAN = "\u001B[36m";

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
        try {
            Socket socket = new Socket(InetAddress.getByName(address), port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());

            Scanner scanner = new Scanner(System.in);

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

                if (serverData.startsWith("AIGUA")) {
                    marcarAigua(request);
                    System.out.println(BLUE + "Resposta: AIGUA" + RESET);

                } else if (serverData.startsWith("TOCAT")) {
                    marcarTocat(request);
                    System.out.println(YELLOW + "Resposta: TOCAT" + RESET);

                } else if (serverData.startsWith("ENFONSAT:")) {
                    String coordenades = serverData.substring("ENFONSAT:".length());
                    marcarEnfonsat(coordenades);
                    System.out.println(RED + "Resposta: ENFONSAT" + RESET);

                } else if (serverData.startsWith("GUANYAT:")) {
                    String coordenades = serverData.substring("GUANYAT:".length());
                    marcarEnfonsat(coordenades);
                    mostrarTaulerClient();
                    System.out.println(RED + "Resposta: ENFONSAT" + RESET);
                    System.out.println(CYAN + "HAS GUANYAT!" + RESET);
                    break;

                } else {
                    System.out.println("Resposta: " + serverData);
                }

                mostrarTaulerClient();
            }

            socket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void marcarAigua(String casella) {
        if (formatCorrecte(casella)) {
            int fila = casella.charAt(0) - 'A';
            int columna = Integer.parseInt(casella.substring(1)) - 1;
            taulerClient[fila][columna] = '~';
        }
    }

    private void marcarTocat(String casella) {
        if (formatCorrecte(casella)) {
            int fila = casella.charAt(0) - 'A';
            int columna = Integer.parseInt(casella.substring(1)) - 1;
            taulerClient[fila][columna] = '1';
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
                System.out.print(" " + colorCasella(taulerClient[i][j]) + " |");
            }
            System.out.println(RESET);
        }

        System.out.print("   +");
        for (int j = 0; j < MIDA; j++) {
            System.out.print("---+");
        }
        System.out.println("\n");
    }

    private String colorCasella(char c) {
        return switch (c) {
            case '~' -> BLUE + c + RESET;     // agua
            case '1' -> YELLOW + c + RESET;   // tocado
            case 'X' -> RED + c + RESET;      // hundido
            case '#' -> WHITE + c + RESET;    // desconocido
            default -> String.valueOf(c);
        };
    }

    public static void main(String[] args) {
        TcpSocketClient client = new TcpSocketClient();
        client.connect("localhost", 9090);
    }
}



