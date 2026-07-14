package DataBaseInteractor;

import Enums.Difficulty;
import Resources.Task;
import Resources.TaskPreview;
import interfaces.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class TaskRepository implements gettable, uploadable, deletable, updatable {
    Connection connection;

    public TaskRepository() throws SQLException {
        connection = DatabaseConnector.connect();
        String sql = "CREATE TABLE IF NOT EXISTS TASKS(id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "description VARCHAR(1000), " +
                "difficulty VARCHAR(20))";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.execute();
    }

    @Override
    public Task getTask(int id) throws SQLException {
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
                    resultSet.getObject("difficulty", Difficulty.class)
            );
        }
    }

    @Override
    public TaskPreview[] getTasks(){
        ArrayList<TaskPreview> tasks = new ArrayList<>();
        String sql = "SELECT id, name, difficulty FROM TASKS";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                tasks.add(new TaskPreview(rs.getInt("id"),
                                          rs.getString("name"),
                                          rs.getObject("difficulty", Difficulty.class)));
            }
        }
        catch (Exception e){
            System.out.println("problem in getTasks() method of TaskRepository class!");
            return null;
        }
        return tasks.toArray(TaskPreview[]::new);
    }
    @Override
    public boolean deleteTask(int id){
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
    public boolean updateTask(Task task, int id){
        String sql = "UPDATE TASKS SET " +
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
    public boolean uploadTask(Task task){
        String sql = "INSERT INTO TASKS VALUES(id = ?, name = ?, description = ?, difficulty = ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, task.getId());
            stmt.setString(2, task.getName());
            stmt.setString(3, task.getDescription());
            stmt.setString(4, task.getDifficulty().name());
            int added = stmt.executeUpdate();
            return added > 0;
        }
        catch (SQLException e){
            System.out.println("problem in uploadTask() method of TaskRepository class");
            return false;
        }
    }
}
