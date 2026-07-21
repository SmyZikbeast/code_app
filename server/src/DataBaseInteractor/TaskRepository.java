package DataBaseInteractor;

import Enums.Difficulty;
import Handlers.SessionHandler;
import Resources.Task;
import Resources.TaskPreview;
import interfaces.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class TaskRepository implements Gettable, Uploadable, Deletable, Updatable {
    Connection connection;
    SessionRepository sessionRepository = new SessionRepository();
    public TaskRepository() throws SQLException {
        this.connection = DatabaseConnector.connect();
    }

    @Override
    public Task getTask(int id, String token) throws SQLException {
        if(sessionRepository.validateToken(token) == 0){
            return null;
        }
        String sql = "SELECT * FROM TASKS WHERE TASKS.ID = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return new Task(resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    Difficulty.valueOf(resultSet.getString("difficulty"))
            );
        }
    }

    @Override
    public TaskPreview[] getTasks(String token){
        if(sessionRepository.validateToken(token) == 0){
            return null;
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
            System.out.println("problem in getTasks() method of TaskRepository class!");
            return null;
        }
        return tasks.toArray(TaskPreview[]::new);
    }
    @Override
    public boolean deleteTask(int id, String token){
        int sessionUserId = sessionRepository.validateToken(token);
        if(sessionUserId == 0){
            return false;
        }

        //todo: валидация пользователя
        String sql = "DELETE FROM TASKS WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            return deleted > 0;
        } catch (SQLException e) {
            System.out.println("problem in deleteTask() method of TaskRepository class!");
            return false;
        }
    }
    @Override
    public boolean updateTask(Task task, int id, String token){
        int sessionUserId = sessionRepository.validateToken(token);
        if(sessionUserId == 0){
            return false;
        }
        int owner_id;
        String sql = "SELECT owner_id FROM TASKS WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            owner_id = rs.getInt("owner_id");
        }
        catch (Exception e){
            System.out.println("Problem in updateTask() method of TaskRepository class!");
            return false;
        }
        if (owner_id != sessionUserId){
            System.out.println("no permission to update task!");
            return false;
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
            return updated > 0;
        }
        catch(SQLException e){
            System.out.println("Problem in updateTask() method of TaskRepository class!");
            return false;
        }
    }
    @Override
    public boolean uploadTask(Task task, String token){
        int sessionUserId = sessionRepository.validateToken(token);
        if(sessionUserId == 0){
            return false;
        }
        String sql = "INSERT INTO TASKS(name, description, difficulty, owner_id) VALUES(?, ?, ?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getDifficulty().name());
            stmt.setInt(4, sessionUserId);
            int added = stmt.executeUpdate();
            return added > 0;
        }
        catch (SQLException e){
            System.out.println("problem in uploadTask() method of TaskRepository class");
            return false;
        }
    }
}
