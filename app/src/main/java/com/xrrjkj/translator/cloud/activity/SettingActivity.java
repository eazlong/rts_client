package com.xrrjkj.translator.cloud.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xrrjkj.translator.cloud.ActivityManager;
import com.xrrjkj.translator.cloud.R;
import com.xrrjkj.translator.cloud.function.UserController;

/**
 * Created by user01 on 2016/12/30.
 */
public class SettingActivity extends BaseFragmentActivity {

    private LinearLayout llyt_back, llyt_change, llyt_version, llyt_logout;
    private ImageView iv_back;
    private TextView tv_title;

    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_white);
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void findViews() {
        llyt_back = (LinearLayout)findViewById(R.id.llyt_menu);
        iv_back = (ImageView)findViewById(R.id.iv_menu);
        iv_back.setImageResource(R.mipmap.ic_back_grey);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.setting));

        llyt_change = (LinearLayout)findViewById(R.id.llyt_change);
        llyt_version = (LinearLayout)findViewById(R.id.llyt_version);
        llyt_logout = (LinearLayout)findViewById(R.id.llyt_logout);

        llyt_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ChangePwdActivity.class);
                startActivity(intent);
            }
        });

        llyt_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast.makeText(getApplicationContext(), "已经是最新版本", Toast.LENGTH_SHORT).show();
            }
        });

        llyt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserController controller = new UserController();
                controller.logout();
                ActivityManager.getInstance().exit();
            }
        });

        llyt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
