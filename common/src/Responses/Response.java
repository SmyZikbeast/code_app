package Responses;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Response {
    public static void send(HttpExchange exchange, int responseCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                responseCode,
                bytes.length
        );

        try (OutputStream os =
                     exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

}
