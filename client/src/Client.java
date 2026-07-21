import Enums.Difficulty;
import Resources.Task;
import Resources.TaskPreview;
import interactor.ServerInteractor;
import manager.AccountManager;

import java.util.Scanner;
public class Client {
    static AccountManager accountManager = new AccountManager();
    public static void main(String[] args) {

        initialize();
        Scanner scanner = new Scanner(System.in);
        ServerInteractor taskManager = new ServerInteractor(accountManager);
        taskManager.uploadTask(new Task("abc", "jopa", Difficulty.Easy));
        taskManager.uploadTask(new Task("def", "jopa2", Difficulty.Easy));
        for(TaskPreview taskPreview: taskManager.requestTasks()){
            System.out.println(taskPreview);
        }
        taskManager.updateTask(new Task("dest", "ldkgjslkgj", Difficulty.Hard), 2);
        while (true){
            System.out.println("request a task");
                int taskId = scanner.nextInt();
                Task task = taskManager.requestTask(taskId);
                System.out.println(task);
        }
    }
    public static void initialize(){
        accountManager.createUser();
        accountManager.register();
        accountManager.authorize();

    }
}