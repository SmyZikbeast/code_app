package interfaces;

import Resources.RepositoryResponse;
import Resources.Task;

public interface Updatable {
    RepositoryResponse updateTask(Task task, int id, String token);
}
