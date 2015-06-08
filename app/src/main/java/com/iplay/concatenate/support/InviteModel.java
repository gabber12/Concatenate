package com.iplay.concatenate.support;

/**
 * Created by gabber12 on 06/04/15.
 */
public class InviteModel extends FriendModel {

    private String message;

    public InviteModel() {
        id = "";
        message = "";
    }

    public InviteModel(String senderId, String message) {
        this.message = message;
        this.id = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return id;
    }

    public void setSenderId(String senderId) {
        this.id = senderId;
    }

    public String toString() {
        return id;
    }
}
