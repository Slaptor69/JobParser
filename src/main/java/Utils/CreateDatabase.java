package Utils;

import java.sql.*;

public class CreateDatabase {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "postgres"; 


    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE jobparser");
                System.out.println("База jobparser успешно создана");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Не найден драйвер PostgreSQL");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании базы: " + e.getMessage());
        }
    }
}
