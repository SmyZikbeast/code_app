package interfaces;

import Resources.Task;

public interface Updatable {
    boolean updateTask(Task task, int id, String token);
}
