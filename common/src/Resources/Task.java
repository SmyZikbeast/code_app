package Resources;

import Enums.Difficulty;

public class Task {
    int id;
    String name;
    String description;
    Difficulty difficulty;
    public Task(){};
    public Task(int id, String name, String desc, Difficulty difficulty){
        this.id = id;
        this.name = name;
        this.description = desc;
        this.difficulty = difficulty;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDescription(String desc){
        this.description = desc;
    }
    public int getId(){
        return this.id;
    }
    public String getDescription(){
        return this.description;
    }
    public String getName(){
        return this.name;
    }
    public Difficulty getDifficulty(){
        return this.difficulty;
    }
    public String toString(){
        return "id: " + this.id + "\n" +
                "name: "+ this.name + "\n" +
                "description:" + this.description;
    }
}
