package Responses;

public class AuthorizationResponse {
    String token;
    public AuthorizationResponse(String token){
        this.token = token;
    }
    public String getToken(){
        return this.token;
    }
}
