package Responses;


import Resources.TaskPreview;

public class ReadAllTasksResponse {
    TaskPreview[] tasks;
    public ReadAllTasksResponse(TaskPreview[] tasks){
        this.tasks = tasks;
    }

    public TaskPreview[] getTasks() {
        return tasks;
    }
}
