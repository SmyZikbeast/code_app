package Requests;

import Resources.User;

public class AuthorizationRequest{
    User user;
    public AuthorizationRequest(User user) {
        this.user = user;
    }
    public AuthorizationRequest(){};
    public User getUser(){
        return this.user;
    }
    public void setUser(User u){
        this.user = u;
    }
}
