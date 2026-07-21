package DataBaseInteractor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    public static Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres?currentSchema=codeapp";
        String user = "postgres";
        String password = "uqCC<8977";
        return DriverManager.getConnection(url, user, password);
    }

}
