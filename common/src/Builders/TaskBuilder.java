package Builders;

import Resources.Task;

public class TaskBuilder {
    private final Task task = new Task();
    public TaskBuilder(){}
    public TaskBuilder setId(int id){
        task.setId(id);
        return this;
    }
    public TaskBuilder setName(String name){
        task.setName(name);
        return this;
    }
    public TaskBuilder setDescription(String desc){
        task.setDescription(desc);
        return this;
    }
    public Task build(){
        return task;
    }
}
