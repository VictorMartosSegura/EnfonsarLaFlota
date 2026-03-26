import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tauler {

    private static final int MIDA = 10;

    private final char[][] tauler;
    private final int[][] idsVaixells;
    private final List<Vaixell> vaixells;
    private final Random random;
    private int seguentId = 1;

    public Tauler() {
        tauler = new char[MIDA][MIDA];
        idsVaixells = new int[MIDA][MIDA];
        vaixells = new ArrayList<>();
        random = new Random();

        omplirAigua();
        colLocarFlota();
    }

    private void omplirAigua() {
        for (int i = 0; i < MIDA; i++) {
            for (int j = 0; j < MIDA; j++) {
                tauler[i][j] = '~';
                idsVaixells[i][j] = 0;
            }
        }
    }

    private void colLocarFlota() {
        colLocarVaixells(4, 1);
        colLocarVaixells(3, 2);
        colLocarVaixells(2, 3);
        colLocarVaixells(1, 4);
    }

    private void colLocarVaixells(int midaVaixell, int quantitat) {
        for (int k = 0; k < quantitat; k++) {
            boolean colocat = false;

            while (!colocat) {
                boolean horitzontal = random.nextBoolean();
                int fila = random.nextInt(MIDA);
                int columna = random.nextInt(MIDA);

                if (potColLocar(fila, columna, midaVaixell, horitzontal)) {
                    posarVaixell(fila, columna, midaVaixell, horitzontal);
                    colocat = true;
                }
            }
        }
    }

    private boolean potColLocar(int fila, int columna, int midaVaixell, boolean horitzontal) {
        if (horitzontal) {
            if (columna + midaVaixell > MIDA) return false;
            for (int j = columna; j < columna + midaVaixell; j++) {
                if (tauler[fila][j] != '~') return false;
            }
        } else {
            if (fila + midaVaixell > MIDA) return false;
            for (int i = fila; i < fila + midaVaixell; i++) {
                if (tauler[i][columna] != '~') return false;
            }
        }
        return true;
    }

    private void posarVaixell(int fila, int columna, int midaVaixell, boolean horitzontal) {
        Vaixell vaixell = new Vaixell(seguentId);

        if (horitzontal) {
            for (int j = columna; j < columna + midaVaixell; j++) {
                tauler[fila][j] = 'B';
                idsVaixells[fila][j] = seguentId;
                vaixell.afegirCasella(fila, j);
            }
        } else {
            for (int i = fila; i < fila + midaVaixell; i++) {
                tauler[i][columna] = 'B';
                idsVaixells[i][columna] = seguentId;
                vaixell.afegirCasella(i, columna);
            }
        }

        vaixells.add(vaixell);
        seguentId++;
    }

    public String processarTirada(String casella) {
        if (!formatCorrecte(casella)) {
            return "FORMAT INCORRECTE. Exemple: A1 o J10";
        }

        int fila = casella.charAt(0) - 'A';
        int columna = Integer.parseInt(casella.substring(1)) - 1;

        if (tauler[fila][columna] == '~') {
            tauler[fila][columna] = 'O';
            return "AIGUA";
        }

        if (tauler[fila][columna] == 'O' || tauler[fila][columna] == 'X') {
            return "CASELLA JA UTILITZADA";
        }

        if (tauler[fila][columna] == 'B') {
            tauler[fila][columna] = 'X';

            int idVaixell = idsVaixells[fila][columna];
            Vaixell vaixell = buscarVaixell(idVaixell);

            if (vaixell != null && vaixell.estaEnfonsat(tauler)) {
                return "ENFONSAT\n" + vaixell.getCoordenadesText();
            }

            return "TOCAT";
        }

        return "ERROR";
    }

    private Vaixell buscarVaixell(int id) {
        for (Vaixell v : vaixells) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
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

    public void mostrarTaulerServidor() {
        System.out.println("\nTAULER SERVIDOR");
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
                System.out.print(" " + tauler[i][j] + " |");
            }
            System.out.println();
        }

        System.out.print("   +");
        for (int j = 0; j < MIDA; j++) {
            System.out.print("---+");
        }
        System.out.println();
    }

    private static class Casella {
        int fila;
        int columna;

        Casella(int fila, int columna) {
            this.fila = fila;
            this.columna = columna;
        }
    }

    private static class Vaixell {
        private final int id;
        private final List<Casella> caselles;

        Vaixell(int id) {
            this.id = id;
            this.caselles = new ArrayList<>();
        }

        int getId() {
            return id;
        }

        void afegirCasella(int fila, int columna) {
            caselles.add(new Casella(fila, columna));
        }

        boolean estaEnfonsat(char[][] tauler) {
            for (Casella c : caselles) {
                if (tauler[c.fila][c.columna] != 'X') {
                    return false;
                }
            }
            return true;
        }

        String getCoordenadesText() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < caselles.size(); i++) {
                Casella c = caselles.get(i);
                sb.append((char) ('A' + c.fila)).append(c.columna + 1);
                if (i < caselles.size() - 1) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }
    }
}