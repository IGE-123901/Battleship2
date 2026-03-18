package battleship;
import java.sql.*;

public class Scoreboard {

    private static final String DB_FILE = "scoreboard.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    // Cria a tabela se ainda não existir
    public Scoreboard() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS games (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    result    TEXT    NOT NULL,
                    shots     INTEGER NOT NULL,
                    date      TEXT    NOT NULL
                )
            """);

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar a base de dados: " + e.getMessage());
        }
    }

    // Guarda o resultado de um jogo
    public void saveGame(String result, int totalShots) {
        String sql = "INSERT INTO games (result, shots, date) VALUES (?, ?, datetime('now'))";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, result);   // "WIN" ou "LOSS"
            pstmt.setInt(2, totalShots);
            pstmt.executeUpdate();
            System.out.println("Resultado guardado no scoreboard!");

        } catch (SQLException e) {
            System.err.println("Erro ao guardar jogo: " + e.getMessage());
        }
    }

    // Mostra o scoreboard na consola
    public void printScoreboard() {
        String sql = "SELECT result, shots, date FROM games ORDER BY id DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Contar vitórias e derrotas
            int wins = 0, losses = 0;
            java.util.List<String[]> rows = new java.util.ArrayList<>();

            while (rs.next()) {
                String result = rs.getString("result");
                String shots  = rs.getString("shots");
                String date   = rs.getString("date");
                rows.add(new String[]{result, shots, date});
                if (result.equals("WIN")) wins++;
                else losses++;
            }

            if (rows.isEmpty()) {
                System.out.println("Ainda não há jogos registados.");
                return;
            }

            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║               SCOREBOARD                    ║");
            System.out.printf ("║  Vitórias: %-5d  Derrotas: %-5d           ║%n", wins, losses);
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.printf ("║  %-8s  %-8s  %-20s  ║%n", "Resultado", "Tiros", "Data");
            System.out.println("╠══════════════════════════════════════════════╣");

            for (String[] row : rows) {
                System.out.printf("║  %-8s  %-8s  %-20s  ║%n", row[0], row[1], row[2]);
            }

            System.out.println("╚══════════════════════════════════════════════╝\n");

        } catch (SQLException e) {
            System.err.println("Erro ao ler scoreboard: " + e.getMessage());
        }
    }
}

