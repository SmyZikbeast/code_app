package Requests;

import Resources.Task;

public class TaskCreateRequest extends Request{
    public TaskCreateRequest(Task task){
        this.task = task;
    }

}
