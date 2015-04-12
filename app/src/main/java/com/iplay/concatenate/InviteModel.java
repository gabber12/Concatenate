package com.iplay.concatenate;

/**
 * Created by gabber12 on 06/04/15.
 */
public class InviteModel {
    private String senderId;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public InviteModel(){ senderId = ""; message="";}
    public InviteModel(String senderId, String message){
        this.message = message;
        this.senderId = senderId;
    }
    public String toString() {
        return senderId;
    }
}
