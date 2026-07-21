package Requests;

import Resources.User;

public class AuthorizationRequest{
    User user;
    public AuthorizationRequest(User user) {
        this.user = user;
    }
    public User getUser(){
        return this.user;
    }
}
