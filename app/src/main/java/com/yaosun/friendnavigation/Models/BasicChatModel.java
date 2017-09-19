package com.yaosun.friendnavigation.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by yaohuasun on 9/4/17.
 */

public class BasicChatModel {

    // BasicChatModel is a class that describes the chat between two users
    // it will be invoked when the user click the recycler view item of a friend in the friend list
    // it is shared between the two friends of the chat

    private String User1EmailAddr;
    private String User2EmailAddr;
    private HashMap<String,Object> MessageIds;
    private String ChatId;
    private HashMap<String,Object> MeetRequest;

    public BasicChatModel()
    {

    }

    public BasicChatModel(String user1EmailAddr, String user2EmailAddr, HashMap<String,Object> messageIds, String chatId, HashMap<String,Object>meetRequest)
    {
        this.User1EmailAddr = user1EmailAddr;
        this.User2EmailAddr = user2EmailAddr;
        // TODO: remove the messageIds field, they could be found from the chatIds
        this.MessageIds = messageIds;
        this.ChatId = chatId;
        this.MeetRequest = meetRequest;
    }

    public void setUser1EmailAddr(String email)
    {
        this.User1EmailAddr = email;
    }

    public void setUser2EmailAddr(String email)
    {
        this.User2EmailAddr = email;
    }

    public void setMessageIds (HashMap<String,Object> messageIds)
    {
        this.MessageIds = messageIds;
    }

    public  void setChatId (String chatId)
    {
        this.ChatId = chatId;
    }

    public void setMeetRequest(HashMap<String,Object> meetRequest){this.MeetRequest = meetRequest;}



    public String getUser1EmailAddr(){
        return this.User1EmailAddr;
    }

    public String getUser2EmailAddr(){
        return this.User2EmailAddr;
    }

    public HashMap<String,Object> getMessageIds(){
        return this.MessageIds;
    }

    public String getChatId(){
        return this.ChatId;
    }

    public HashMap<String,Object> getMeetRequest(){return this.MeetRequest;}

}
