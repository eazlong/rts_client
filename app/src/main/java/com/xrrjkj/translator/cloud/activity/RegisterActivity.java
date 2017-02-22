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

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user01 on 2016/12/30.
 */
public class RegisterActivity extends BaseFragmentActivity {
    private Button btn_register;

    private LinearLayout llyt_back;
    private ImageView iv_back, iv_head;
    private TextView tv_title;
    private EditText ed_email, ed_pwd, ed_username;

    private String email, pwd, username;

    private int [] imgs ={R.mipmap.head1, R.mipmap.head2, R.mipmap.head3, R.mipmap.head4, R.mipmap.head5, R.mipmap.head6, R.mipmap.head7, R.mipmap.head8};
    private int index;
    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_white);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void findViews() {
        btn_register = (Button) findViewById(R.id.btn_register);
        llyt_back = (LinearLayout)findViewById(R.id.llyt_menu);
        iv_back = (ImageView)findViewById(R.id.iv_menu);
        iv_head = (ImageView)findViewById(R.id.iv_head);
        iv_back.setImageResource(R.mipmap.ic_back_grey);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.register));

        ed_email = (EditText)findViewById(R.id.ed_email);
        ed_pwd = (EditText)findViewById(R.id.ed_pwd);
        ed_username = (EditText)findViewById(R.id.ed_username);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = ed_email.getText().toString().trim();
                pwd = ed_pwd.getText().toString().trim();
                username = ed_username.getText().toString().trim();
                String regEx ="^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(email);

                String regex="^[a-zA-Z0-9\u4E00-\u9FA5]+$";
                Pattern pattern = Pattern.compile(regex);
                Matcher match=pattern.matcher(username);
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_tip1), Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(pwd)){
                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_tip2), Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(username)){
                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_tip5), Toast.LENGTH_SHORT).show();
                }else if(!m.matches()){
                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_tip3), Toast.LENGTH_SHORT).show();
                }else if(pwd.length()<6){
                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_tip4), Toast.LENGTH_SHORT).show();
                }else if(!match.matches()){
                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_tip6), Toast.LENGTH_SHORT).show();
                }else{
                    UserController controller = new UserController();
                    controller.register(username, email, pwd, index);
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), getString(R.string.register_ok), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        llyt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = new Random().nextInt(8);
                iv_head.setImageResource(imgs[index]);
            }
        });
    }
}
