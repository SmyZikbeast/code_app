package interfaces;

import Resources.RepositoryResponse;
import Resources.Task;
import Resources.TaskPreview;

import java.sql.SQLException;

public interface Gettable {
    RepositoryResponse<Task> getTask(int id, String token) throws SQLException;
    RepositoryResponse<TaskPreview[]> getTasks(String token) throws SQLException;
}
