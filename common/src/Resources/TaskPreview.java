package Resources;

import Enums.Difficulty;

public class TaskPreview {
    int id;
    String name;
    Difficulty difficulty;
    public TaskPreview(int id, String name, Difficulty difficulty){
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
    }
    public String toString(){
        return this.id + " " + this.name + " " + this.difficulty.name();
    }
}
