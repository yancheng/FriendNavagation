package com.yaosun.friendnavigation.Models;

/**
 * Created by yaohuasun on 9/5/17.
 */

public class MessageModel {
    private String SenderEmail;
    private String Message;
    private String Timestamp;
    public MessageModel(){

    }

    //Constructor for plain text message
    public MessageModel(String sender, String message, String time) {
        this.SenderEmail = sender;
        this.Message = message;
        this.Timestamp = time;
    }

    public String getSenderEmail() {
        return this.SenderEmail;
    }
    public String getTimestamp(){
        return this.Timestamp;
    }

    public String getMessage() {
        return this.Message;
    }
}


