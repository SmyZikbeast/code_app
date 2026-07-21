package Requests;

import Resources.Task;

public abstract class TaskRequest extends Request{
    Task task;
    public Task getTask(){
        return this.task;
    }
}
