package Requests;

import Resources.Task;

public class TaskCreateRequest extends TaskRequest{
    public TaskCreateRequest(Task task){
        this.task = task;
    }
}
