package com.xrrjkj.translator.cloud.function;

import com.xrrjkj.translator.cloud.SharedPreferencesUtils;

import java.util.Random;

/**
 * Created by user01 on 2017/1/9.
 */
public class UserController {

    public void register(String userName, String email, String password, int head){
        Random random=new Random();
        SharedPreferencesUtils.getEdit().putString("userName", userName).putString("email",email).putString("password", password).putString("userId", String.valueOf(System.currentTimeMillis())+String.valueOf(random.nextInt(10000))).commit();
        SharedPreferencesUtils.getEdit().putInt("head", head).commit();
    }

    public boolean login(String email, String password){
        boolean isOk = false;
        if(email.equals(SharedPreferencesUtils.getSharedPreferences().getString("email", "")) && password.equals(SharedPreferencesUtils.getSharedPreferences().getString("password", ""))){
            SharedPreferencesUtils.getEdit().putBoolean("isLogin", true).commit();
            isOk = true;
        }
        return isOk;
    }

    public boolean isLogin(){
        return  SharedPreferencesUtils.getSharedPreferences().getBoolean("isLogin", false);
    }

    public void logout(){
        SharedPreferencesUtils.getEdit().putBoolean("isLogin", false).commit();
    }

    public String getUserName(){
        return  SharedPreferencesUtils.getSharedPreferences().getString("userName", "");
    }

    public String getUserId(){
        return  SharedPreferencesUtils.getSharedPreferences().getString("userId", "");
    }

    public String getEmail(){
        return  SharedPreferencesUtils.getSharedPreferences().getString("email", "");
    }

    public String getPassword(){
        return  SharedPreferencesUtils.getSharedPreferences().getString("password", "");
    }

    public int getHead(){
        return  SharedPreferencesUtils.getSharedPreferences().getInt("head", 0);
    }

    public boolean changePwd(String oldPwd, String newPwd){
        boolean isOk =false;
        if(oldPwd.equals(getPassword())){
            SharedPreferencesUtils.getEdit().putString("password", newPwd).commit();
            isOk =true;
        }
        return isOk;
    }

}
