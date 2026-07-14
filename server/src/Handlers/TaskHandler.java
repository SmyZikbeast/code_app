package Handlers;

import DataBaseInteractor.TaskRepository;
import Requests.TaskCreateRequest;
import Requests.TaskUpdateRequest;
import Resources.Task;
import Resources.TaskPreview;
import Responses.ReadAllTasksResponse;
import Responses.ReadTaskResponse;
import Responses.Response;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class TaskHandler implements HttpHandler {
    static Gson gson = new Gson();
    TaskRepository repository;

    public TaskHandler() throws SQLException {
        repository = new TaskRepository();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch(method){
            case "GET" -> get(exchange);
            case "POST" -> post(exchange);
            case "PUT" -> put(exchange);
            case "DELETE" -> delete(exchange);
            default -> error(exchange);
        }
    }
    void get(HttpExchange exchange) throws IOException{
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String[] tokens = path.split("/");

        if(tokens.length == 3){
            TaskPreview[] tasks = repository.getTasks();
            ReadAllTasksResponse wrapper = new ReadAllTasksResponse(tasks);
            Response.send(exchange, 200, gson.toJson(wrapper));
        }
        if(tokens.length == 4){
            String id = tokens[3];
            try {
                Task task = repository.getTask(Integer.parseInt(id));
                if (task == null){
                    Response.send(exchange, 404, "");
                }
                else {
                    ReadTaskResponse wrapper = new ReadTaskResponse(task);
                    Response.send(exchange, 200, gson.toJson(wrapper));
                }
            }
            catch (NumberFormatException e){
                Response.send(exchange, 404, "");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    void post(HttpExchange exchange) throws IOException {
        String body;
        try(InputStream is = exchange.getRequestBody()){
            body = new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
        TaskCreateRequest request = gson.fromJson(body, TaskCreateRequest.class);
        boolean success = repository.uploadTask(request.getTask());
        if (success){
            Response.send(exchange, 201, "");
        }
        else{
            Response.send(exchange, 400, "");
        }
    }
    void put(HttpExchange exchange) throws IOException{
        String body;
        try(InputStream is = exchange.getRequestBody()){
            body = new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
        TaskUpdateRequest request = gson.fromJson(body, TaskUpdateRequest.class);
        boolean success = repository.updateTask(request.getTask(), request.getId());
        if (success){
            Response.send(exchange, 204, "");
        }
        else{
            Response.send(exchange, 400, "");
        }
    }
    void delete(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String[] tokens = path.split("/");

        if(tokens.length == 4){
            String id = tokens[3];
            boolean success;
            try {
                success = repository.deleteTask(Integer.parseInt(id));
            }
            catch (NumberFormatException e){
                Response.send(exchange, 404, "");
                return;
            }
            if (success){
                Response.send(exchange, 204, "");
            }
            else{
                Response.send(exchange, 404, "");
            }
        }
    }
    void error(HttpExchange exchange) throws IOException {
        Response.send(exchange, 400, "");
    }
}
