package Requests;

import Resources.Task;

public abstract class Request {
    Task task;
    public Task getTask(){
        return this.task;
    }
}
