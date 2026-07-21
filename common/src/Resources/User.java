package Resources;

public class User {
    int id = 0;
    String username;
    String password;

    public User(String name, String password) {
        this.username = name;
        this.password = password;
    }
    public User(int id,String name, String password) {
        this.id = id;
        this.username = name;
        this.password = password;
    }
    public int getId(){
        return this.id;
    }
    public String getUsername() {
        return this.username;
    }
    public String getPassword(){
        return this.password;
    }
}
