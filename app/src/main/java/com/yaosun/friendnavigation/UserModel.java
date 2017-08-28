package com.yaosun.friendnavigation;

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

    public String getUserEmail()
    {
        return this.emailAddr;
    }
}
