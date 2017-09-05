package com.yaosun.friendnavigation.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaohuasun on 9/4/17.
 */

public class BasicChatModel {

    // BasicChatModel is a class that describes the chat between two users
    // it will be invoked when the user click the recycler view item of a friend in the friend list
    // it is shared between the two friends of the chat

    private String User1EmailAddr;
    private String User2EmailAddr;
    private List<String> MessageIds;
    private String ChatId;

    public BasicChatModel()
    {

    }

    public BasicChatModel(String user1EmailAddr, String user2EmailAddr, List<String> messageIds, String chatId)
    {
        this.User1EmailAddr = user1EmailAddr;
        this.User2EmailAddr = user2EmailAddr;
        // TODO: remove the messageIds field, they could be found from the chatIds
        this.MessageIds = messageIds;
        this.ChatId = chatId;
    }

    public void setUser1EmailAddr(String email)
    {
        this.User1EmailAddr = email;
    }

    public void setUser2EmailAddr(String email)
    {
        this.User2EmailAddr = email;
    }

    public void setMessageIds (List<String> messageIds)
    {
        this.MessageIds = messageIds;
    }
    public  void setChatId (String chatId)
    {
        this.ChatId = chatId;
    }

    public String getUser1EmailAddr(){
        return this.User1EmailAddr;
    }

    public String getUser2EmailAddr(){
        return this.User2EmailAddr;
    }

    public List<String> getMessageIds(){
        return this.MessageIds;
    }

    public String getChatId(){
        return this.ChatId;
    }

}
