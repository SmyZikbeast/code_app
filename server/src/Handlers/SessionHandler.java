package Handlers;

import DataBaseInteractor.SessionRepository;
import Enums.Code;
import Requests.AuthorizationRequest;
import Resources.RepositoryResponse;
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
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Session-Token");
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "POST" -> post(exchange); // authorization
                case "GET" -> get(exchange);
                case "PUT" -> put(exchange);
                case "DELETE" -> delete(exchange);
                case "OPTIONS" -> options(exchange);
                default -> error(exchange);
            }
        }
        catch (Exception e){
            Response.send(exchange, new RepositoryResponse<>(Code.SERVER_ERROR));
            System.out.println("problem in SessionHandler class");
            e.printStackTrace();
        }

    }
    public void post(HttpExchange exchange){
        String body;
        try(InputStream is = exchange.getRequestBody()) {
            body = new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8
            );
            AuthorizationRequest request = gson.fromJson(body, AuthorizationRequest.class);
            RepositoryResponse<String> response = repository.authorize(request);
            Response.send(exchange, response);
        }
        catch (IOException e) {
            System.out.println("problem in post method of SessionHandler class");
        }
    }
    public void get(HttpExchange exchange) throws IOException {
        //Response.send(exchange, 405, "");
    }
    public void put(HttpExchange exchange) throws IOException {
        //Response.send(exchange, 405, "");
    }
    public void delete(HttpExchange exchange) throws IOException {
        //Response.send(exchange, 405, "");
    }
    public void error(HttpExchange exchange) throws IOException {
        //Response.send(exchange, 405, "");
    }
    public void options(HttpExchange exchange) throws IOException {
        Response.send(exchange, new RepositoryResponse<>(Code.OK));
    }
}
