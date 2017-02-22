package com.xrrjkj.translator.cloud.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.xrrjkj.translator.cloud.ActivityManager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by user01 on 2016/11/14.
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
    protected FragmentManager mFragmentManager;
    public ArrayList<String> hideWhiteTagList;

//    public VaryViewHelper mVaryViewHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        ActivityManager.getInstance().addActivity(this);
        setContentView();
//        try{
//            mVaryViewHelper = new VaryViewHelper.Builder()
//                    .setDataView(findViewById(R.id.vary_content))//放数据的父布局，逻辑处理在该Activity中处理
//                    .setEmptyView(LayoutInflater.from(this).inflate(R.layout.view_not_data, null))//加载页，无实际逻辑处理
//                    .build();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        findViews();

    }

    public void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void setContentView ();
    protected abstract void findViews ();

    public Fragment replaceFragment (int holderId, Class fragClazz, Class[] paramTypes, Object[] params) {
        if (mFragmentManager == null) {
            return null;
        }

        Fragment fragment = mFragmentManager.findFragmentByTag(getTag(fragClazz));
        if (fragment != null) {
            return fragment;
        }

        if ((fragment = createFragment(fragClazz, paramTypes, params)) != null) {
            mFragmentManager.beginTransaction().replace(holderId, fragment, getTag(fragClazz)).commit();
        }

        return fragment;
    }

    public Fragment createFragment (Class fragClazz, Class[] paramTypes, Object[] params) {
        try {
            Constructor constructor = fragClazz.getConstructor(paramTypes);
            Fragment fragment = (Fragment) constructor.newInstance(params);
            return fragment;
        } catch (Exception e) {
            Log.e("wl", "failed to create instance of class : " + fragClazz.getName());
        }

        return null;
    }

    protected String getTag (Class clazz) {
        return clazz.getSimpleName();
    }

    public Fragment showFragment (boolean hideOthers,
                                  List<String> whiteTagList,
                                  int holderId,
                                  Class fragClazz,
                                  Class[] paramTypes,
                                  Object[] params) {

        Fragment fragment = addFragment(holderId, fragClazz, paramTypes, params);
        if (fragment == null) {
            return null;
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (hideOthers) {
            List<Fragment> fragments = mFragmentManager.getFragments();
            if (fragments != null) {
                for (Fragment frag : fragments) {
                    String tag = frag.getTag();
                    if (!TextUtils.isEmpty(tag) && whiteTagList != null && whiteTagList.contains(tag)) {
                        continue;
                    }
                    transaction.hide(frag);
                }
            }
        }

        transaction.show(fragment);
        transaction.commit();
        return fragment;
    }

    public Fragment addFragment (int holderId, Class fragClazz, Class[] paramTypes, Object[] params) {
        if (mFragmentManager == null) {
            return null;
        }

        Fragment fragment = mFragmentManager.findFragmentByTag(getTag(fragClazz));
        if (fragment == null) {
            if ((fragment = createFragment(fragClazz, paramTypes, params)) != null) {
                mFragmentManager.beginTransaction().add(holderId, fragment, getTag(fragClazz)).commit();
            }
        }
        return fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().removeActivity(this);
    }
}
