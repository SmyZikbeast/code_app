package Handlers;

import DataBaseInteractor.TaskRepository;
import Enums.Code;
import Requests.TaskCreateRequest;
import Requests.TaskUpdateRequest;
import Resources.RepositoryResponse;
import Resources.Task;
import Resources.TaskPreview;
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
    static final Gson gson = new Gson();
    TaskRepository repository;

    public TaskHandler() throws SQLException {
        repository = new TaskRepository();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Session-Token");
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> get(exchange);
                case "POST" -> post(exchange);
                case "PUT" -> put(exchange);
                case "DELETE" -> delete(exchange);
                case "OPTIONS" -> options(exchange);
                default -> error(exchange);
            }
        }
        catch(Exception e){
            System.out.println("fell to exception");
            e.printStackTrace();
            Response.send(exchange, new RepositoryResponse<>(Code.SERVER_ERROR));
        }
    }

    void get(HttpExchange exchange) throws IOException{
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String[] tokens = path.split("/");
        if(tokens.length == 3){
            RepositoryResponse<TaskPreview[]> tasks = repository.getTasks(exchange.getRequestHeaders().getFirst("Session-Token"));
            Response.send(exchange, tasks);
        }
        if(tokens.length == 4){
            String id = tokens[3];
            try {
                RepositoryResponse<Task> task = repository.getTask(Integer.parseInt(id), exchange.getRequestHeaders().getFirst("Session-Token"));
                Response.send(exchange, task);
            }
            catch (NumberFormatException e){
                System.out.println("nfe");
                Response.send(exchange, new RepositoryResponse<>(Code.SERVER_ERROR));
            } catch (SQLException e) {
                System.out.println("sql exception in get method of taskHandler class");
                Response.send(exchange, new RepositoryResponse<>(Code.SERVER_ERROR));
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
        var response = repository.uploadTask(request.getTask(), exchange.getRequestHeaders().getFirst("Session-Token"));
        Response.send(exchange, response);
    }

    void put(HttpExchange exchange) throws IOException{
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String[] tokens = path.split("/");
        if(tokens.length == 4) {
            String body;
            try (InputStream is = exchange.getRequestBody()) {
                body = new String(
                        is.readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }
            TaskUpdateRequest request = gson.fromJson(body, TaskUpdateRequest.class);
            var response = repository.updateTask(request.getTask(), Integer.parseInt(tokens[3]), exchange.getRequestHeaders().getFirst("Session-Token"));
            Response.send(exchange, response);
            return;
        }
        Response.send(exchange, new RepositoryResponse<>(Code.BAD_REQUEST));
    }

    void delete(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String[] tokens = path.split("/");

        if(tokens.length == 4){
            String id = tokens[3];
            try {
                var response = repository.deleteTask(Integer.parseInt(id), exchange.getRequestHeaders().getFirst("Session-Token"));
                Response.send(exchange, response);
            }
            catch (NumberFormatException e){
                Response.send(exchange, new RepositoryResponse<>(Code.NOT_FOUND));
            }
        }
    }

    void error(HttpExchange exchange) throws IOException {
        //Response.send(exchange, 400, "");
    }

    public void options(HttpExchange exchange) throws IOException {
        Response.send(exchange, new RepositoryResponse<>(Code.OK));
    }
}
