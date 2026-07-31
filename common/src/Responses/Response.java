package Responses;

import Enums.Code;
import Resources.RepositoryResponse;
import Resources.TaskPreview;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Response {
    private final static Gson gson = new Gson();

    public static <T> void send(HttpExchange exchange, RepositoryResponse<T> response) throws IOException {
        int statusCode = getCode(response.getCode());
        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );
        if (statusCode == 204) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }
        String json = gson.toJson(response.getBody());
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );

        try (OutputStream os =
                     exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static int getCode(Code code){
        switch(code){
            case OK -> {
                return 200;
            }
            case CREATED -> {
                return 201;
            }
            case NO_CONTENT -> {
                return 204;
            }
            case BAD_REQUEST -> {
                return 400;
            }
            case WRONG_PASSWORD, UNAUTHORIZED, TOKEN_EXPIRED -> {
                return 401;
            }
            case NO_PERMISSION -> {
                return 403;
            }
            case NOT_FOUND -> {
                return 404;
            }
            case USERNAME_TAKEN -> {
                return 409;
            }
            default -> {
                return 500;
            }
        }
    }

}
