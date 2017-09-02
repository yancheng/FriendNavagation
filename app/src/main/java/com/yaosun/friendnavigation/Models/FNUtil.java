package com.yaosun.friendnavigation.Models;

/**
 * Created by yaohuasun on 9/1/17.
 */

public class FNUtil {
    public void FNUtil(){

    }

    static public String encodeEmail (String email){
        return email.replace(".",",");
    }
}
