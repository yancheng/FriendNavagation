package com.yaosun.friendnavigation.Models;

/**
 * Created by Yaohua on 8/28/2017.
 */

public class UserModel {

    private String emailAddr;
    private String passwordForLogin;
    private String userKey;
    // TODO: add other user details, such as user name
    // and phone number, sex, profile picture, etc.

    // TODO: remove passwordForLogin and userKey member

    public UserModel(String email, String passwordForLogin, String userKey)
    {
        this.emailAddr = email;
        this.passwordForLogin = passwordForLogin;
        this.userKey = userKey;
    }

    public UserModel(){
        this("","","");
    }

    public String getEmailAddr()
    {
        return this.emailAddr;
    }

    public void setEmailAddr(String email){this.emailAddr = email;}

    public String getPasswordForLogin(){return this.passwordForLogin;}

    public void setPasswordForLogin(String password){this.passwordForLogin = password;}

    public String getUserKey(){return this.getUserKey();}

    public void setUserKey(String key){this.userKey = key;}

}
