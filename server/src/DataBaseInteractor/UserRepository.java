package DataBaseInteractor;

import Enums.Code;
import Requests.RegistrationRequest;
import Requests.UserUpdateRequest;
import Resources.RepositoryResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Enums.Code.*;

public class UserRepository {
    Connection connection;
    SessionRepository sessionRepository = new SessionRepository();
    public UserRepository() throws SQLException {
        this.connection = DatabaseConnector.connect();
    }
    public RepositoryResponse<Integer> register(RegistrationRequest requestWrapper) {
        String username = requestWrapper.getUser().getUsername();
        String password = requestWrapper.getUser().getPassword();
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "SELECT * FROM USERS WHERE USERS.username = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new RepositoryResponse<>(USERNAME_TAKEN);
            }
        } catch(Exception e){
            System.out.println("problem in register method of userRepository class");
            return new RepositoryResponse<>(SERVER_ERROR);
        }

        sql = "INSERT INTO USERS(username, password) VALUES(?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashed);
            stmt.executeUpdate();
        } catch(Exception e){
            System.out.println("problem in register method of userRepository class");
            return new RepositoryResponse<>(SERVER_ERROR);
        }

        sql = "SELECT id FROM USERS WHERE USERS.username = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return new RepositoryResponse<>(OK, rs.getInt("id"));
            }
            return new RepositoryResponse<>(SERVER_ERROR);
        }
        catch(Exception e){
            System.out.println("problem in register method of userRepository class");
            return new RepositoryResponse<>(SERVER_ERROR);
        }
    }

    public RepositoryResponse<Void> update(UserUpdateRequest requestWrapper, String token) {
        Code code = sessionRepository.validateToken(token).getCode();
        if(code != OK){
            return new RepositoryResponse<>(code);
        }
        String username = requestWrapper.getUser().getUsername();
        String password = requestWrapper.getUser().getPassword();
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql;
        PreparedStatement stmt;
        ResultSet rs;
        try {
            int id = sessionRepository.validateToken(token).getBody();
            sql = "SELECT id FROM USERS WHERE username = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()){
                if(rs.getInt("id") != id){
                    return new RepositoryResponse<>(USERNAME_TAKEN);
                }
            }
            sql = "UPDATE USERS SET username = ?, password = ? WHERE id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashed);
            stmt.setInt(3, id);
            int changed = stmt.executeUpdate();
            if (changed > 0){
                return new RepositoryResponse<>(NO_CONTENT);
            }
            return new RepositoryResponse<>(SERVER_ERROR);
        }
        catch (Exception e){
            System.out.println("problem in update method of userRepository class");
            return new RepositoryResponse<>(SERVER_ERROR);
        }
    }

    public RepositoryResponse<Void> delete(int id, String token) {
        int sessionUserId = sessionRepository.validateToken(token).getBody();
        Code code = sessionRepository.validateToken(token).getCode();
        if(code != OK){
            return new RepositoryResponse<>(code);
        }
        if(id != sessionUserId){
            return new RepositoryResponse<>(NO_PERMISSION);
        }
        String sql;
        PreparedStatement stmt;
        try {
            sql = "DELETE FROM USERS WHERE id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, sessionUserId);
            int changed = stmt.executeUpdate();
            if (changed > 0){
                return new RepositoryResponse<>(NO_CONTENT);
            }
            return new RepositoryResponse<>(SERVER_ERROR);
        }
        catch (Exception e){
            System.out.println("problem in delete method of userRepository class");
            return new RepositoryResponse<>(SERVER_ERROR);
        }
    }
}
