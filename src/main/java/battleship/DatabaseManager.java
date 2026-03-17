package battleship;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:battleship.db";
    private Connection conn;

    public DatabaseManager() throws SQLException {
        // Estabelece a ligação e cria a tabela se não existir
        conn = DriverManager.getConnection(DB_URL);
        createTable();
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS jogadas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "timestamp TEXT," +
                "tiro TEXT," +
                "resultado TEXT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void guardarJogada(String tiroJson, String resultado) throws SQLException {
        String sql = "INSERT INTO jogadas (timestamp, tiro, resultado) VALUES (datetime('now'), ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tiroJson);
            pstmt.setString(2, resultado);
            pstmt.executeUpdate();
        }
    }

    public void listarJogadas() throws SQLException {
        String sql = "SELECT * FROM jogadas";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n=== HISTÓRICO DE JOGADAS (BD) ===");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                        rs.getString("timestamp") + " | " +
                        rs.getString("tiro") + " | " +
                        rs.getString("resultado"));
            }
            System.out.println("================================\n");
        }
    }

    public void fechar() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}