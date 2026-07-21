package DataBaseInteractor;

import Requests.RegistrationRequest;
import Requests.UserUpdateRequest;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    Connection connection;
    SessionRepository sessionRepository = new SessionRepository();
    public UserRepository() throws SQLException {
        this.connection = DatabaseConnector.connect();
    }
    public int register(RegistrationRequest requestWrapper) {
        String username = requestWrapper.getUser().getUsername();
        String password = requestWrapper.getUser().getPassword();
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        try {
            String sql = "SELECT * FROM USERS WHERE USERS.username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return 0;
            }

            sql = "INSERT INTO USERS(username, password) VALUES(?, ?)";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashed);
            stmt.executeUpdate();

            sql = "SELECT id FROM USERS WHERE USERS.username = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()){
                return rs.getInt("id");
            }
            return 0;
        }
        catch(Exception e){
            System.out.println("problem in register method of userRepository class");
            return 0;
        }
    }

    public boolean update(UserUpdateRequest requestWrapper, String token) {
        int sessionUserId = sessionRepository.validateToken(token);
        if(sessionUserId == 0){
            return false;
        }
        String username = requestWrapper.getUser().getUsername();
        String password = requestWrapper.getUser().getPassword();
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql;
        PreparedStatement stmt;
        ResultSet rs;
        try {
            sql = "SELECT user_id FROM SESSIONS WHERE token = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if(!rs.next()) {
                return false;
            }
            int id = rs.getInt("user_id");
            sql = "SELECT id FROM USERS WHERE username = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()){
                if(rs.getInt("id") != id){
                    return false;
                }
            }
            sql = "UPDATE USERS SET username = ?, password = ? WHERE id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashed);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
        catch (Exception e){
            System.out.println("problem in update method of userRepository class");
            return false;
        }
    }

    public boolean delete(int id, String token) {
        int sessionUserId = sessionRepository.validateToken(token);
        if(sessionUserId == 0){
            return false;
        }
        //todo: посмотреть все ли тут ок
        String sql;
        PreparedStatement stmt;
        ResultSet rs;
        try {
            sql = "SELECT user_id FROM SESSIONS WHERE token = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if(!rs.next()) {
                return false;
            }
            int userId = rs.getInt("user_id");

            sql = "DELETE FROM USERS WHERE id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
        catch (Exception e){
            System.out.println("problem in delete method of userRepository class");
            return false;
        }
    }
}
