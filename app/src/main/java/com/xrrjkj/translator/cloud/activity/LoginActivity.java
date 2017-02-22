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

import com.xrrjkj.translator.cloud.ActivityManager;
import com.xrrjkj.translator.cloud.R;
import com.xrrjkj.translator.cloud.function.UserController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user01 on 2016/12/30.
 */
public class LoginActivity extends BaseFragmentActivity {
    private Button btn_login;

    private LinearLayout llyt_back;
    private ImageView iv_back;
    private TextView tv_title, tv_forget;
    private EditText ed_email, ed_pwd;

    private String email, pwd;
    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_white);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void findViews() {
        btn_login = (Button) findViewById(R.id.btn_login);
        llyt_back = (LinearLayout)findViewById(R.id.llyt_menu);
        iv_back = (ImageView)findViewById(R.id.iv_menu);
        iv_back.setImageResource(R.mipmap.ic_back_grey);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_forget = (TextView)findViewById(R.id.tv_forget);

        ed_email = (EditText)findViewById(R.id.ed_email);
        ed_pwd = (EditText)findViewById(R.id.ed_pwd);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = ed_email.getText().toString().trim();
                pwd = ed_pwd.getText().toString().trim();
                String regEx ="^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(email);
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_tip1), Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(pwd)){
                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_tip2), Toast.LENGTH_SHORT).show();
                }else if(!m.matches()){
                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_tip3), Toast.LENGTH_SHORT).show();
                }else if(pwd.length()<6){
                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_tip4), Toast.LENGTH_SHORT).show();
                }else{
                    login();
                }
            }
        });

        tv_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPwdActivity.class);
                startActivity(intent);
            }
        });

        llyt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getInstance().exit();
            }
        });
    }

    private void login(){
        UserController controller = new UserController();
        if(controller.login(email, pwd)){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager.getInstance().exit();
    }
}
