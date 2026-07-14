package Requests;

import Resources.Task;

public class TaskUpdateRequest extends Request{
    int id;
    public TaskUpdateRequest(Task task, int id){
        this.task = task;
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
}
