package com.xrrjkj.translator.cloud.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xrrjkj.translator.cloud.R;
import com.xrrjkj.translator.cloud.adapter.LanguageListAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user01 on 2016/12/30.
 */
public class LanguageListActivity extends BaseFragmentActivity {

    private LinearLayout llyt_back;
    private ImageView iv_back;
    private TextView tv_title;

    private ListView mListView;
    private LanguageListAdapter mAdapter;
    private String [] languageCode;
    private String [] language;
    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_white);
        setContentView(R.layout.activity_language_list);
    }

    @Override
    protected void findViews() {
        llyt_back = (LinearLayout)findViewById(R.id.llyt_menu);
        iv_back = (ImageView)findViewById(R.id.iv_menu);
        iv_back.setImageResource(R.mipmap.ic_cancle);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.language_choose));

        mListView = (ListView)findViewById(R.id.listView);
        initView();

        llyt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String lan = language[position];
                String lanCode = languageCode[position];
                Intent intent = new Intent();
                intent.putExtra("language", lan);
                intent.putExtra("code", lanCode);
                LanguageListActivity.this.setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initView(){
        language = getResources().getStringArray(R.array.languages_zh);
        languageCode = getResources().getStringArray(R.array.languages);
        mAdapter = new LanguageListAdapter(language, LanguageListActivity.this);
        mListView.setAdapter(mAdapter);
    }
}
