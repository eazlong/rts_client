package com.xrrjkj.translator.cloud;

import android.app.Application;
import android.content.Context;


/**
 * Created by user01 on 2016/11/15.
 */
public class XrApplication extends Application {

    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

    }

    public static Context getContext() {
        return mContext;
    }
}
