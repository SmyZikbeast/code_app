package Handlers;

import DataBaseInteractor.SessionRepository;
import Requests.AuthorizationRequest;
import Responses.AuthorizationResponse;
import Responses.Response;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class SessionHandler implements HttpHandler {
    static final Gson gson = new Gson();
    SessionRepository repository;

    public SessionHandler() throws SQLException {
        this.repository = new SessionRepository();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "POST" -> post(exchange); // authorization
                case "GET" -> get(exchange);
                case "PUT" -> put(exchange); // continue session
                case "DELETE" -> delete(exchange); // close session
                default -> error(exchange);
            }
        }
        catch (Exception e){
            Response.send(exchange, 500, "");
            System.out.println("problem in SessionHandler class");
        }

    }
    public void post(HttpExchange exchange){
        String body;
        try(InputStream is = exchange.getRequestBody()) {
            body = new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8
            );
            AuthorizationRequest requestWrapper = gson.fromJson(body, AuthorizationRequest.class);
            String token = repository.authorize(requestWrapper);
            if (token != null){
                Response.send(exchange, 200, gson.toJson(new AuthorizationResponse(token)));
            }
            else{
                Response.send(exchange, 401, "");
            }
        }
        catch (IOException e) {
            System.out.println("problem in post method of SessionHandler class");
        }
    }
    public void get(HttpExchange exchange) throws IOException {
        Response.send(exchange, 405, "");
    }
    public void put(HttpExchange exchange) throws IOException {
        Response.send(exchange, 405, "");
    }
    public void delete(HttpExchange exchange) throws IOException {
        Response.send(exchange, 405, "");
    }
    public void error(HttpExchange exchange) throws IOException {
        Response.send(exchange, 405, "");
    }
}
