package interactor;

import Requests.TaskCreateRequest;
import Requests.TaskUpdateRequest;
import Resources.Task;
import Resources.TaskPreview;
import Resources.User;
import Responses.ReadAllTasksResponse;
import Responses.ReadTaskResponse;
import com.google.gson.Gson;
import manager.AccountManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerInteractor {
    private final Gson gson = new Gson();
    private final AccountManager accountManager;
    public ServerInteractor(AccountManager manager){
        this.accountManager = manager;
    }
    public Task requestTask(int id) {
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tasks/" + id))
                    .header("Session-Token", accountManager.getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ReadTaskResponse taskResponse = gson.fromJson(response.body(), ReadTaskResponse.class);
            return taskResponse.getTask();
        } catch (IOException | InterruptedException e) {
            System.out.println("problem in method requestTask of serverInteractor class");
            return null;
        }
    }
    public TaskPreview[] requestTasks(){
        try(HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tasks/"))
                    .header("Session-Token", accountManager.getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ReadAllTasksResponse taskResponse = gson.fromJson(response.body(), ReadAllTasksResponse.class);
            return taskResponse.getTasks();
        }
        catch(Exception e){
            System.out.println("problem in method requestTasks of ServerInteractor class");
            return null;
        }
    }
    public boolean uploadTask(Task task){
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tasks/"))
                    .header("Content-Type", "application/json")
                    .header("Session-Token", accountManager.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new TaskCreateRequest(task))))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 201;
        } catch (Exception e) {
            System.out.println("problem in uploadTask method of class ServerInteractor");
            return false;
        }
    }
    public boolean updateTask(Task task, int id){
        try(HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tasks/" + id))
                    .header("Content-Type", "application/json")
                    .header("Session-Token", accountManager.getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(new TaskUpdateRequest(task))))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204;
        }
        catch (Exception e){
            System.out.println("problem in updateTask method of class ServerInteractor");
            return false;
        }
    }



}
