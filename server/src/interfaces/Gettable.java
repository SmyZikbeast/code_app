package interfaces;

import Resources.Task;
import Resources.TaskPreview;

import java.sql.SQLException;

public interface Gettable {
    Task getTask(int id, String token) throws SQLException;
    TaskPreview[] getTasks(String token) throws SQLException;
}
