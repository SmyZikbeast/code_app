package Handlers;

import DataBaseInteractor.UserRepository;
import Requests.RegistrationRequest;
import Requests.UserUpdateRequest;
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
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "POST" -> post(exchange); // registration
                case "GET" -> get(exchange); // get user
                case "PUT" -> put(exchange); // update user
                case "DELETE" -> delete(exchange); // delete user
                default -> error(exchange);
            }
        }
        catch (Exception e){
            System.out.println("problem in UserHandler class");
        }
    }

    public void post(HttpExchange exchange){
        String body;
        try(InputStream is = exchange.getRequestBody()) {
            body = new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8
            );
            RegistrationRequest requestWrapper = gson.fromJson(body, RegistrationRequest.class);
            int id = repository.register(requestWrapper);
            if (id != 0){
                Response.send(exchange, 201, gson.toJson(new RegistrationResponse(id)));
            }
            else{
                Response.send(exchange, 404, "");
            }
        }
        catch (IOException e) {
            System.out.println("problem in post method of UserHandler class");
        }
    }

    public void get(HttpExchange exchange) throws IOException {
        Response.send(exchange, 405, "");
    }

    public void put(HttpExchange exchange){
        String body;
        try(InputStream is = exchange.getRequestBody()) {
            body = new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            UserUpdateRequest requestWrapper = gson.fromJson(body, UserUpdateRequest.class);
            boolean success = repository.update(requestWrapper, exchange.getRequestHeaders().getFirst("Session-token"));

            if (success){
                Response.send(exchange, 201, "");
            }
            else{
                Response.send(exchange, 401, "");
            }
        }
        catch (IOException e) {
            System.out.println("problem in put method of UserHandler class");
        }
    }

    public void delete(HttpExchange exchange){
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String[] tokens = path.split("/");
        try {
            if (tokens.length == 3) {
                Response.send(exchange, 405, "");
                return;
            }

            boolean success = repository.delete(Integer.parseInt(tokens[3]), exchange.getRequestHeaders().getFirst("Session-token"));

            if (success) {
                Response.send(exchange, 201, "");
            } else {
                Response.send(exchange, 401, "");
            }
        }
        catch (Exception e){
            System.out.println("problem in delete method of userHandler class");
        }

    }

    public void error(HttpExchange exchange) throws IOException {
        Response.send(exchange, 405, "");
    }

}
