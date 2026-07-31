package DataBaseInteractor;

import Enums.Code;
import Enums.Difficulty;
import Resources.RepositoryResponse;
import Resources.Task;
import Resources.TaskPreview;
import interfaces.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static Enums.Code.*;


public class TaskRepository implements Gettable, Uploadable, Deletable, Updatable {
    Connection connection;
    SessionRepository sessionRepository = new SessionRepository();
    public TaskRepository() throws SQLException {
        this.connection = DatabaseConnector.connect();
    }

    @Override
    public RepositoryResponse<Task> getTask(int id, String token) throws SQLException {
        Code code = sessionRepository.validateToken(token).getCode();
        if(code != OK){
            return new RepositoryResponse<>(code);
        }
        String sql = "SELECT * FROM TASKS WHERE TASKS.ID = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                return new RepositoryResponse<>(NOT_FOUND);
            }
            return new RepositoryResponse<>(OK,
                    new Task (resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    Difficulty.valueOf(resultSet.getString("difficulty"))
                )
            );
        }
    }

    @Override
    public RepositoryResponse<TaskPreview[]> getTasks(String token){
        Code code = sessionRepository.validateToken(token).getCode();
        if(code != OK){
            return new RepositoryResponse<>(code);
        }
        ArrayList<TaskPreview> tasks = new ArrayList<>();
        String sql = "SELECT id, name, difficulty FROM TASKS";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                tasks.add(new TaskPreview(rs.getInt("id"),
                                          rs.getString("name"),
                                          Difficulty.valueOf(rs.getString("difficulty"))));
            }
        }
        catch (Exception e){
            return new RepositoryResponse<>(SERVER_ERROR);
        }
        return new RepositoryResponse<>(OK, tasks.toArray(TaskPreview[]::new));
    }

    @Override
    public RepositoryResponse<Void> deleteTask(int id, String token){
        Code code = sessionRepository.validateToken(token).getCode();
        int userId = sessionRepository.validateToken(token).getBody();
        if(code != OK){
            return new RepositoryResponse<>(code);
        }
        String sql = "SELECT owner_id FROM TASKS WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()){
                return new RepositoryResponse<>(NOT_FOUND);
            }
            int owner_id = rs.getInt("owner_id");
            if (owner_id != userId){
                return new RepositoryResponse<>(NO_PERMISSION);
            }
        } catch (SQLException e){
            return new RepositoryResponse<>(SERVER_ERROR);
        }
        sql = "DELETE FROM TASKS WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            if (deleted > 0){
                return new RepositoryResponse<>(OK);
            }
            return new RepositoryResponse<>(NOT_FOUND);
        } catch (SQLException e) {
            System.out.println("problem in deleteTask() method of TaskRepository class!");
            return new RepositoryResponse<>(SERVER_ERROR);
        }
    }

    @Override
    public RepositoryResponse<Void> updateTask(Task task, int id, String token){
        Code code = sessionRepository.validateToken(token).getCode();
        int userId = sessionRepository.validateToken(token).getBody();
        if(code != OK){
            return new RepositoryResponse<>(code);
        }
        String sql = "SELECT owner_id FROM TASKS WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()){
                return new RepositoryResponse<>(NOT_FOUND);
            }
            int ownerId = rs.getInt("owner_id");
            if (ownerId != userId){
                return new RepositoryResponse<>(NO_PERMISSION);
            }
        } catch (SQLException e){
            return new RepositoryResponse<>(SERVER_ERROR);
        }
        sql = "UPDATE TASKS SET " +
                "name = ?, " +
                "description = ?, " +
                "difficulty = ? WHERE " +
                "id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getDifficulty().name());
            stmt.setInt(4, id);
            int updated = stmt.executeUpdate();
            if (updated > 0){
                return new RepositoryResponse<>(OK);
            }
            return new RepositoryResponse<>(NOT_FOUND);
        }
        catch(SQLException e){
            System.out.println("Problem in updateTask() method of TaskRepository class!");
            return new RepositoryResponse<>(SERVER_ERROR);
        }
    }

    @Override
    public RepositoryResponse<Void> uploadTask(Task task, String token){
        Code code = sessionRepository.validateToken(token).getCode();
        int userId = sessionRepository.validateToken(token).getBody();
        if(code != OK){
            return new RepositoryResponse<>(code);
        }
        String sql = "INSERT INTO TASKS(name, description, difficulty, owner_id) VALUES(?, ?, ?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getDifficulty().name());
            stmt.setInt(4, userId);
            int added = stmt.executeUpdate();
            if (added > 0){
                return new RepositoryResponse<>(OK);
            }
            return new RepositoryResponse<>(SERVER_ERROR);
        }
        catch (SQLException e){
            System.out.println("problem in uploadTask() method of TaskRepository class");
            return new RepositoryResponse<>(SERVER_ERROR);
        }
    }
}
