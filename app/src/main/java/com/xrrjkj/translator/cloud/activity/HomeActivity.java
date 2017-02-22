package com.xrrjkj.translator.cloud.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xrrjkj.translator.cloud.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user01 on 2016/12/30.
 */
public class HomeActivity extends BaseFragmentActivity implements View.OnClickListener{

    private ImageView iv_user, iv_setting;
    private LinearLayout llyt_menu1, llyt_menu2, llyt_menu3;
    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_white);
        setContentView(R.layout.activity_homepage);
    }

    @Override
    protected void findViews() {
        iv_user = (ImageView)findViewById(R.id.iv_user);
        iv_setting = (ImageView)findViewById(R.id.iv_setting);
        llyt_menu1 = (LinearLayout)findViewById(R.id.llyt_menu1);
        llyt_menu2 = (LinearLayout)findViewById(R.id.llyt_menu2);
        llyt_menu3 = (LinearLayout)findViewById(R.id.llyt_menu3);

        iv_user.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        llyt_menu1.setOnClickListener(this);
        llyt_menu2.setOnClickListener(this);
        llyt_menu3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.iv_user:
                intent = new Intent(HomeActivity.this, PersonalCenterActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_setting:
                intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
                break;

            case R.id.llyt_menu1:
                intent = new Intent(HomeActivity.this, SingleChatActivity.class);
                startActivity(intent);
                break;

            case R.id.llyt_menu2:
                intent = new Intent(HomeActivity.this, GroupChatActivity.class);
                startActivity(intent);
                break;

            case R.id.llyt_menu3:
                intent = new Intent(HomeActivity.this, RoomChooseListActivity.class);
                startActivity(intent);
                break;
        }
    }
}
