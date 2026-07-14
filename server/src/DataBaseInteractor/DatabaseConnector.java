package DataBaseInteractor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    public static Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/my_database";
        String user = "";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

}
