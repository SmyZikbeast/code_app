import manager.TaskManager;

import java.util.Scanner;
public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        initialize();
        TaskManager taskManager = new TaskManager();
        while (true){
            System.out.println("request a task");
            int taskId = scanner.nextInt();
            taskManager.requestTask(taskId);
            System.out.println(taskManager.getTask(taskId));
        }
    }
    public static void initialize(){

    }
}