package manager;

import Resources.Task;
import interactor.ServerInteractor;

import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> taskMap = new HashMap<>();
    ServerInteractor serverInteractor = new ServerInteractor();
    public void requestTask(int id){
        if (!taskMap.containsKey(id)) {
            taskMap.put(id, serverInteractor.requestTask(id));
        }
    }
    public Task getTask(int id){
        return taskMap.get(id);
    }
}
