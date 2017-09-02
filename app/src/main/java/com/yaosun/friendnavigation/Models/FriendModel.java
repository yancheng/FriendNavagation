package com.yaosun.friendnavigation.Models;

/**
 * Created by yaohuasun on 9/1/17.
 */

public class FriendModel {
    private String friendEmailAddr;

    public FriendModel(String email){
        this.friendEmailAddr = email;
    }

    public FriendModel(){
        this("");
    }

    public String getFriendEmailAddr(){
        return this.friendEmailAddr;
    }

    public void setFriendEmailAddr(String email){
        this.friendEmailAddr = email;
    }
}
