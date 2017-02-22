package com.xrrjkj.translator.cloud.activity;

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
public class PersonalCenterActivity extends BaseFragmentActivity {
    private Button btn_change;

    private LinearLayout llyt_back;
    private ImageView iv_back, iv_head;
    private TextView tv_title, tv_username, tv_email;

    private int [] imgs ={R.mipmap.head1, R.mipmap.head2, R.mipmap.head3, R.mipmap.head4, R.mipmap.head5, R.mipmap.head6, R.mipmap.head7, R.mipmap.head8};
    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_white);
        setContentView(R.layout.activity_personal_center);
    }

    @Override
    protected void findViews() {
        btn_change = (Button) findViewById(R.id.btn_change);
        llyt_back = (LinearLayout)findViewById(R.id.llyt_menu);
        iv_back = (ImageView)findViewById(R.id.iv_menu);
        iv_head = (ImageView)findViewById(R.id.iv_head);
        iv_back.setImageResource(R.mipmap.ic_back_grey);
        int index = new UserController().getHead();
        iv_head.setImageResource(imgs[index]);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.center));

        tv_username = (TextView)findViewById(R.id.tv_name);
        tv_email = (TextView)findViewById(R.id.tv_email);
        UserController controller = new UserController();
        tv_username.setText(controller.getUserName());
        tv_email.setText(controller.getEmail());

        btn_change.setBackgroundResource(R.drawable.btn_bg_gray);
        btn_change.setOnClickListener(new View.OnClickListener() {
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
