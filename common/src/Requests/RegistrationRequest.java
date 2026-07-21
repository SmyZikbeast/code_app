package Requests;

import Resources.User;

public class RegistrationRequest {
    User user;
    public RegistrationRequest(User user){
        this.user = user;
    }
    public User getUser(){
        return this.user;
    }
}
