import DataBaseInteractor.DatabaseInitializer;
import Handlers.SessionHandler;
import Handlers.TaskHandler;
import Handlers.UserHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.sql.SQLException;

public class Server {
    public static void main(String[] args) throws IOException, SQLException {
        DatabaseInitializer.initialize();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/tasks", new TaskHandler());
        server.createContext("/api/users", new UserHandler());
        server.createContext("/api/sessions", new SessionHandler());
        server.setExecutor(null);
        server.start();
    }
}
