package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Утильный класс для получения JDBC-соединения из config.properties.
 */
public class DatabaseManager {
    private static final String URL;
    private static final String USER;
    private static final String PASS;

    static {
        try (var in = DatabaseManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            Properties props = new Properties();
            props.load(in);
            URL  = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASS = props.getProperty("db.password");
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить соединение с БД", e);
        }
    }
}
