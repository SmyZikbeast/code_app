package manager;

import Requests.*;
import Resources.User;
import Responses.AuthorizationResponse;
import Responses.RegistrationResponse;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class AccountManager {
    private final Gson gson = new Gson();
    protected String token;
    String name;
    String password;
    int id;
    public AccountManager(){

    }
    public void createUser(){
            Scanner scanner = new Scanner(System.in);
            System.out.println("username:");
            name = scanner.next();
            System.out.println("password");
            password = scanner.next();
    }

    public String getToken(){
        return this.token;
    }

    public void authorize(){
        User user = new User(name, password);
        AuthorizationRequest wrapper = new AuthorizationRequest(user);
        try(HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/sessions/"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(wrapper)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            AuthorizationResponse responseWrapper = gson.fromJson(response.body(), AuthorizationResponse.class);
            this.token = responseWrapper.getToken();
        }
        catch (Exception e){
            System.out.println("problem in authorize method of accountManager class");
        }
    }

    public boolean register(){
        User user = new User(name, password);
        RegistrationRequest requestWrapper = new RegistrationRequest(user);
        try(HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/users/"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestWrapper)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 201) {
                this.id = gson.fromJson(response.body(), RegistrationResponse.class).getId();
                return true;
            }
            return false;
        }
        catch (Exception e){
            System.out.println("problem in register method of AccountManager class");
            return false;
        }
    }

    public boolean updateUser(User user){
        try(HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/users/" + this.id))
                    .header("Content-Type", "application/json")
                    .header("Session-Token", token)
                    .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(new UserUpdateRequest(user))))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        }
        catch (Exception e){
            System.out.println("problem in updateTask method of class ServerInteractor");
            return false;
        }
    }

    public boolean deleteUser(){
        try(HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/users/" + this.id))
                    .header("Content-Type", "application/json")
                    .header("Session-Token", token)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        }
        catch (Exception e){
            System.out.println("problem in updateTask method of class ServerInteractor");
            return false;
        }
    }
}
