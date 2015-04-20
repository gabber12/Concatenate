package com.iplay.concatenate;

/**
 * Created by gabber12 on 18/04/15.
 */
public class FriendModel {
    public int Score;
    public FriendModel(String name, String id) {
        Name = name;
        this.id = id;
    }

    public FriendModel( String name, String id, int score) {
        Score = score;
        Name = name;
        this.id = id;
    }

    @Override
    public String toString() {

        return Name;
    }
    private String Name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    private String id;

}
