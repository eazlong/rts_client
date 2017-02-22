package com.xrrjkj.translator.cloud;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SharedPreferencesUtils {
    public static String SP = "c_chat";

    public static Editor getEdit() {
        SharedPreferences sp = XrApplication.getContext().getSharedPreferences(
                SP, Context.MODE_PRIVATE);
        return sp.edit();
    }

    public static SharedPreferences getSharedPreferences() {
        return XrApplication.getContext().getSharedPreferences(SP,
                Context.MODE_PRIVATE);
    }


}
