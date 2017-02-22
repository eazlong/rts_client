package com.xrrjkj.translator.cloud.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xrrjkj.translator.cloud.R;
import com.xrrjkj.translator.cloud.function.UserController;

/**
 * Created by user01 on 2016/12/30.
 */
public class StartActivity extends BaseFragmentActivity {
    private Button btn_login, btn_register;
    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_blue2);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void findViews() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);

        UserController controller = new UserController();
        if(controller.isLogin()){
            btn_login.setVisibility(View.GONE);
            btn_register.setVisibility(View.GONE);
            mHandler.sendEmptyMessageDelayed(101, 1500);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 101:
                    Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
}
