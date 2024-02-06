package exchanger.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private ConnectionManager() {
    }

    public static Connection open() {
        try {
            URL resource = ConnectionManager.class.getClassLoader().getResource("currency_exchange.db");
            String path = new File(resource.toURI()).getAbsolutePath();
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException | URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
