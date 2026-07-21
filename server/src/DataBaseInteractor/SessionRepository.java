package DataBaseInteractor;

import Requests.AuthorizationRequest;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessionRepository {
    Connection connection;

    public SessionRepository() throws SQLException {
        this.connection = DatabaseConnector.connect();
    }
    public String authorize(AuthorizationRequest requestWrapper) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String username = requestWrapper.getUser().getUsername();
        String password = requestWrapper.getUser().getPassword();

        String sql;
        PreparedStatement stmt;
        ResultSet rs;
        try {
            sql = "SELECT id, password FROM USERS WHERE username = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if(!rs.next()){
                return null;
            }
            String storedHash = rs.getString("password");
            if (!BCrypt.checkpw(password, storedHash)){
                return null;
            }
            int userId = rs.getInt("id");
            sql = "INSERT INTO SESSIONS (token, user_id, expiration_time) VALUES (?, ?, ?)";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, token);
            stmt.setInt(2, userId);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)));
            int added = stmt.executeUpdate();
            if(added > 0){
                return token;
            }
        }
        catch (Exception e){
            return null;
        }
    return null;
    }

    public int validateToken(String token){
        try {
            String sql = "SELECT user_id, expiration_time FROM SESSIONS WHERE token = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                return 0;
            }
            int id = rs.getInt("user_id");
            LocalDateTime expTime = rs.getTimestamp("expiration_time").toLocalDateTime();
            if (expTime.isBefore(LocalDateTime.now())){
                return 0;
            }
            sql = "UPDATE SESSIONS SET expiration_time = ? WHERE token = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)));
            stmt.setString(2, token);
            stmt.executeUpdate();
            return id;
        }
        catch (Exception e){
            return 0;
        }
    }
}
