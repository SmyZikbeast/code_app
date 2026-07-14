package interfaces;

import Resources.Task;
import Resources.TaskPreview;

import java.sql.SQLException;

public interface gettable {
    Task getTask(int id) throws SQLException;
    TaskPreview[] getTasks() throws SQLException;
}
