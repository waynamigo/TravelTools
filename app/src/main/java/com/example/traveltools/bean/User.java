package com.example.traveltools.bean;


import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by waynamigo on 18-5-23.
 */

public class User implements Serializable{
    private String name;
    private String password;
    private String phonenumber;
    private String jwt;
    public String getName(){
        return this.name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name){
        this.name =name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
    public static void saveLoginInfo(Context context, String username, String password){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences("data", context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor=sharedPre.edit();
        //设置参数
        editor.putString("username", username);
        editor.putString("password", password);
        //提交
        editor.commit();
    }
}
