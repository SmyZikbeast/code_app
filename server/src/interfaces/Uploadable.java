package interfaces;

import Resources.Task;

public interface Uploadable {
    boolean uploadTask(Task task, String token);
}
