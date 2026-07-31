package interfaces;

import Resources.RepositoryResponse;
import Resources.Task;

public interface Uploadable {
    RepositoryResponse uploadTask(Task task, String token);
}
