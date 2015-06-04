package com.iplay.concatenate;

import java.util.Comparator;

/**
 * Created by gabber12 on 18/04/15.
 */
public class FriendModel  implements Comparable{
    public int Score;

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }
    public FriendModel() { Score = 0; id = ""; Name= "";}
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
    public String Name;

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

    public String id;



    @Override
    public int compareTo(Object another) {
        return ((FriendModel)another).getScore() - getScore();
    }
}
