package com.iplay.concatenate.support;

/**
 * Created by gabber12 on 18/04/15.
 */
public class FriendModel implements Comparable {
    public int Score;
    public String Name;
    public String id;

    public FriendModel() {
        Score = 0;
        id = "";
        Name = "";
    }

    public FriendModel(String name, String id) {
        Name = name;
        this.id = id;
    }

    public FriendModel(String name, String id, int score) {
        Score = score;
        Name = name;
        this.id = id;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    @Override
    public String toString() {

        return Name;
    }

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

    @Override
    public int compareTo(Object another) {
        return ((FriendModel) another).getScore() - getScore();
    }
}
