package Requests;

import Resources.Task;

public class TaskUpdateRequest extends TaskRequest{
    public TaskUpdateRequest(Task task){
        this.task = task;
    }
}
