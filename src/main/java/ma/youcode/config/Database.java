package ma.youcode.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection instance;
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_biblio";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        if (instance == null)
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        else
            return instance;
    }
}