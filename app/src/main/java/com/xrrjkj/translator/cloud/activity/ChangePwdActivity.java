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
import com.xrrjkj.translator.cloud.function.UserController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user01 on 2016/12/30.
 */
public class ChangePwdActivity extends BaseFragmentActivity {
    private Button btn_confirm;

    private LinearLayout llyt_back;
    private ImageView iv_back;
    private TextView tv_title;
    private EditText ed_old, ed_new, ed_new2;

    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_white);
        setContentView(R.layout.activity_change_pwd);
    }

    @Override
    protected void findViews() {
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        llyt_back = (LinearLayout)findViewById(R.id.llyt_menu);
        iv_back = (ImageView)findViewById(R.id.iv_menu);
        iv_back.setImageResource(R.mipmap.ic_back_grey);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.change_pwd));

        ed_old = (EditText)findViewById(R.id.ed_old);
        ed_new = (EditText)findViewById(R.id.ed_new);
        ed_new2 = (EditText)findViewById(R.id.ed_new2);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = ed_old.getText().toString().trim();
                String newPwd = ed_new.getText().toString().trim();
                String newPwd2 = ed_new2.getText().toString().trim();
                if(TextUtils.isEmpty(oldPwd)){
                    Toast.makeText(getApplicationContext(), getString(R.string.change_pwd_input_error_tip1), Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(newPwd)){
                    Toast.makeText(getApplicationContext(), getString(R.string.change_pwd_input_error_tip2), Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(newPwd2)){
                    Toast.makeText(getApplicationContext(), getString(R.string.change_pwd_input_error_tip3), Toast.LENGTH_SHORT).show();
                }else if(newPwd.length()<6 || newPwd2.length()<6){
                    Toast.makeText(getApplicationContext(), getString(R.string.change_pwd_input_error_tip4), Toast.LENGTH_SHORT).show();
                }else if(!newPwd.equals(newPwd2)){
                    Toast.makeText(getApplicationContext(), getString(R.string.change_pwd_input_error_tip5), Toast.LENGTH_SHORT).show();
                }else{
                    UserController controller = new UserController();
                    if(controller.changePwd(oldPwd, newPwd)){
                        Intent intent = new Intent(ChangePwdActivity.this, LoginActivity.class);
                        startActivity(intent);
                        controller.logout();
                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.change_pwd_fail), Toast.LENGTH_SHORT).show();
                    }
                }

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
