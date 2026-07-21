package DataBaseInteractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initialize(){
        try(Connection connection = DatabaseConnector.connect()) {


            String sql = "CREATE TABLE IF NOT EXISTS USERS(id SERIAL PRIMARY KEY, " +
                    "username VARCHAR NOT NULL, " +
                    "password VARCHAR NOT NULL)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.execute();

            sql = "CREATE TABLE IF NOT EXISTS TASKS(id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "description VARCHAR(1000), " +
                    "difficulty VARCHAR(20) NOT NULL, " +
                    "owner_id INT NOT NULL REFERENCES USERS(ID))";
            stmt = connection.prepareStatement(sql);
            stmt.execute();

            sql = "DROP TABLE IF EXISTS SESSIONS";
            stmt = connection.prepareStatement(sql);
            stmt.execute();

            sql = "CREATE TABLE SESSIONS(token VARCHAR(32) PRIMARY KEY, " +
                    "user_id INT NOT NULL REFERENCES USERS(ID), " +
                    "expiration_time TIMESTAMP)";
            stmt = connection.prepareStatement(sql);
            stmt.execute();

            sql = "CREATE INDEX IF NOT EXISTS username_idx ON USERS(username)";
            stmt = connection.prepareStatement(sql);
            stmt.execute();
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("problem initializing database!");
        }
    }
}
