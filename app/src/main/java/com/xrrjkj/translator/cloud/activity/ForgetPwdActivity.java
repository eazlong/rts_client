package com.xrrjkj.translator.cloud.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xrrjkj.translator.cloud.R;

/**
 * Created by user01 on 2016/12/30.
 */
public class ForgetPwdActivity extends BaseFragmentActivity {
    private Button btn_send;

    private LinearLayout llyt_back;
    private ImageView iv_back;
    private TextView tv_title, tv_tip;

    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_white);
        setContentView(R.layout.activity_forget_pwd);
    }

    @Override
    protected void findViews() {
        btn_send = (Button) findViewById(R.id.btn_send);
        llyt_back = (LinearLayout)findViewById(R.id.llyt_menu);
        iv_back = (ImageView)findViewById(R.id.iv_menu);
        iv_back.setImageResource(R.mipmap.ic_back_grey);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.center));

        tv_tip = (TextView)findViewById(R.id.tv_tip);

        btn_send.setBackgroundResource(R.drawable.btn_bg_gray);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.not_support), Toast.LENGTH_SHORT).show();
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
