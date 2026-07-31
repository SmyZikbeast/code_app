package Handlers;

import DataBaseInteractor.UserRepository;
import Enums.Code;
import Requests.RegistrationRequest;
import Requests.UserUpdateRequest;
import Resources.RepositoryResponse;
import Responses.RegistrationResponse;
import Responses.Response;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class UserHandler implements HttpHandler {
    static final Gson gson = new Gson();
    UserRepository repository;

    public UserHandler() throws SQLException {
        this.repository = new UserRepository();
    }

    @Override
    public void handle(HttpExchange exchange){
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Session-Token");
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "POST" -> post(exchange); // registration
                case "GET" -> get(exchange); // get user
                case "PUT" -> put(exchange); // update user
                case "DELETE" -> delete(exchange); // delete user
                case "OPTIONS" -> options(exchange);
                default -> error(exchange);
            }
        }
        catch (Exception e){
            System.out.println("problem in UserHandler class");
        }
    }

    public void post(HttpExchange exchange) throws IOException {
        String body;
        try(InputStream is = exchange.getRequestBody()) {
            body = new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8
            );
            RegistrationRequest requestWrapper = gson.fromJson(body, RegistrationRequest.class);
            var response = repository.register(requestWrapper);
            Response.send(exchange, response);
        }
        catch (IOException e) {
            System.out.println("problem in post method of UserHandler class");
            Response.send(exchange, new RepositoryResponse<>(Code.SERVER_ERROR));
        }
    }

    public void get(HttpExchange exchange) throws IOException {
        //Response.send(exchange, 405, "");
    }

    public void put(HttpExchange exchange) throws IOException {
        String body;
        try(InputStream is = exchange.getRequestBody()) {
            body = new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            UserUpdateRequest requestWrapper = gson.fromJson(body, UserUpdateRequest.class);
            var response = repository.update(requestWrapper, exchange.getRequestHeaders().getFirst("Session-Token"));
            Response.send(exchange, response);
        }
        catch (IOException e) {
            System.out.println("problem in put method of UserHandler class");
            Response.send(exchange, new RepositoryResponse<>(Code.SERVER_ERROR));
        }
    }

    public void delete(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String[] tokens = path.split("/");
        try {
            if (tokens.length == 3) {
                Response.send(exchange, new RepositoryResponse<>(Code.BAD_REQUEST));
                return;
            }

            var response = repository.delete(Integer.parseInt(tokens[3]), exchange.getRequestHeaders().getFirst("Session-Token"));
            Response.send(exchange, response);
        }
        catch (Exception e){
            System.out.println("problem in delete method of userHandler class");
            Response.send(exchange, new RepositoryResponse<>(Code.SERVER_ERROR));
        }

    }

    public void error(HttpExchange exchange) throws IOException {
        //Response.send(exchange, 405, "");
    }

    public void options(HttpExchange exchange) throws IOException {
        Response.send(exchange, new RepositoryResponse<>(Code.OK));
    }
}
