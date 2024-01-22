package git.scathiesgit.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String DB_URL = "url.db";

    private ConnectionManager() {
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection(ApplicationProperties.get(DB_URL));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
