package com.xrrjkj.translator.cloud;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

public class ActivityManager extends Application {

    ArrayList<Activity> mylist = new ArrayList<Activity>();
    ArrayList<FragmentActivity> myfragmentlist = new ArrayList<FragmentActivity>();
    private static ActivityManager instance;

    public ActivityManager() {
    }

    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    public void addActivity(FragmentActivity fragmentActivity) {
        myfragmentlist.add(fragmentActivity);
    }

    public void addActivity(Activity activity) {
        mylist.add(activity);
    }

    public void removeActivity(Activity activity) {
        mylist.remove(activity);
    }

    public void removeActivity(FragmentActivity fragmentActivity) {
        myfragmentlist.remove(fragmentActivity);
    }

    public void exit() {
        try {
            for (Activity activity : mylist) {
                activity.finish();
            }
            for (FragmentActivity fragmentActivity : myfragmentlist) {
                fragmentActivity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}