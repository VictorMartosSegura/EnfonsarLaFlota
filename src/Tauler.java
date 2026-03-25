import java.util.Random;

public class Tauler {
    public static final int MIDA = 10;
    private final char[][] tauler;
    private final Random random;

    public Tauler() {
        tauler = new char[MIDA][MIDA];
        random = new Random();
        omplirAigua();
        colLocarFlota();
    }

    private void omplirAigua() {
        for (int i = 0; i < MIDA; i++) {
            for (int j = 0; j < MIDA; j++) {
                tauler[i][j] = '~';
            }
        }
    }

    private void colLocarFlota() {
        colLocarVaixell(4, 1);
        colLocarVaixell(3, 2);
        colLocarVaixell(2, 3);
        colLocarVaixell(1, 4);
    }

    private void colLocarVaixell(int mida, int quantitat) {
        for (int k = 0; k < quantitat; k++) {
            boolean colocat = false;

            while (!colocat) {
                boolean horitzontal = random.nextBoolean();
                int fila = random.nextInt(MIDA);
                int columna = random.nextInt(MIDA);

                if (potColLocar(fila, columna, mida, horitzontal)) {
                    posarVaixell(fila, columna, mida, horitzontal);
                    colocat = true;
                }
            }
        }
    }

    private boolean potColLocar(int fila, int columna, int mida, boolean horitzontal) {
        if (horitzontal) {
            if (columna + mida > MIDA) return false;
            for (int j = columna; j < columna + mida; j++) {
                if (tauler[fila][j] != '~') return false;
            }
        } else {
            if (fila + mida > MIDA) return false;
            for (int i = fila; i < fila + mida; i++) {
                if (tauler[i][columna] != '~') return false;
            }
        }
        return true;
    }

    private void posarVaixell(int fila, int columna, int mida, boolean horitzontal) {
        if (horitzontal) {
            for (int j = columna; j < columna + mida; j++) {
                tauler[fila][j] = 'B';
            }
        } else {
            for (int i = fila; i < fila + mida; i++) {
                tauler[i][columna] = 'B';
            }
        }
    }

    public char[][] getTauler() {
        return tauler;
    }

    public void mostrarTauler() {
        System.out.print("    ");
        for (int j = 1; j <= MIDA; j++) {
            System.out.printf("%2d  ", j);
        }
        System.out.println();

        for (int i = 0; i < MIDA; i++) {
            System.out.print("  +");
            for (int j = 0; j < MIDA; j++) {
                System.out.print("---+");
            }
            System.out.println();

            System.out.printf("%c |", (char) ('A' + i));
            for (int j = 0; j < MIDA; j++) {
                System.out.print(" " + tauler[i][j] + " |");
            }
            System.out.println();
        }

        System.out.print("  +");
        for (int j = 0; j < MIDA; j++) {
            System.out.print("---+");
        }
        System.out.println();
    }
    public String disparar(int fila, int col) {

        if (fila < 0 || fila >= MIDA || col < 0 || col >= MIDA) {
            return "FORA DE TAULER";
        }

        if (tauler[fila][col] == 'B') {
            tauler[fila][col] = 'X';
            return "TOCAT";
        }

        if (tauler[fila][col] == '~') {
            tauler[fila][col] = 'O';
            return "AIGUA";
        }

        return "JA TIRAT";
    }
    public boolean noQuedenBarcos() {
        for (int i = 0; i < MIDA; i++) {
            for (int j = 0; j < MIDA; j++) {
                if (tauler[i][j] == 'B') {
                    return false;
                }
            }
        }
        return true;
    }


}