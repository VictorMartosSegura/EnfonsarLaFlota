import java.io.*;
import java.net.Socket;

public class ServidorThread extends Thread {

    private Socket socket;
    private Tauler tauler;

    public ServidorThread(Socket socket) {
        this.socket = socket;
        this.tauler = new Tauler(); // tablero aleatorio
    }

    @Override
    public void run() {

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintStream out = new PrintStream(socket.getOutputStream());

            out.println("Benvingut a Enfonsar la flota!");
            out.flush();

            tauler.mostrarTauler();

            String missatge;
            boolean gameOver = false;

            while (!gameOver && (missatge = in.readLine()) != null) {

                missatge = missatge.toUpperCase();

                String resposta = processarTirada(missatge);

                tauler.mostrarTauler();

                out.println(resposta);
                out.flush();

                if (tauler.noQuedenBarcos()) {
                    out.println("HAS GUANYAT!");
                    out.flush();
                    gameOver = true;
                }
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processarTirada(String casella) {
        try {
            int fila = casella.charAt(0) - 'A';
            int col = Integer.parseInt(casella.substring(1)) - 1;

            return tauler.disparar(fila, col);

        } catch (Exception e) {
            return "FORMAT INCORRECTE (ex: A1)";
        }
    }
}