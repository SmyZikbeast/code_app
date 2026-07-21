package Requests;

import Resources.User;

public class UserRequest extends Request{
    User user;
    public User getUser(){
        return this.user;
    }
}
