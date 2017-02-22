package com.xrrjkj.translator.cloud.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xrrjkj.translator.cloud.AudioRawData;
import com.xrrjkj.translator.cloud.AudioRecorderRunnable;
import com.xrrjkj.translator.cloud.ControlThread;
import com.xrrjkj.translator.cloud.ProcessSpeexRunnable;
import com.xrrjkj.translator.cloud.R;
import com.xrrjkj.translator.cloud.RTMPRunnable;
import com.xrrjkj.translator.cloud.adapter.GroupChatListAdapter;
import com.xrrjkj.translator.cloud.bean.ChatInfo;
import com.xrrjkj.translator.cloud.function.UserController;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by user01 on 2017/1/3.
 */
public class JoinGroupChatActivity extends BaseFragmentActivity implements ProcessSpeexRunnable.ProcessSpeexListener , RTMPRunnable.RTMPListener{

    private LinearLayout llyt_back, llyt_language;
    private ListView mListView;
    private GroupChatListAdapter mAdapter;
    private TextView tv_tip, tv_language, tv_room_id;
    private String language, lanCode;
    private String [] languageCodes;
    private String [] languages;
    private ImageView iv_mic;

    private List<ChatInfo> list;

    private int command_type=2;
    private boolean isRecording;
    List<byte[]> mCurrentRecordData = null;
    private TextToSpeech mSpeech = null;

    private AudioRecorderRunnable mAudioRunnable;
    private ProcessSpeexRunnable mProcessSpeexRunnable;
    private RTMPRunnable mRTMPRunnable;

    private static final int STOP_RECORDING = 0;
    ControlThread clientThread;
    Handler handler;

    private int reStartNum =0;
    private String userId;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STOP_RECORDING:
                    stopRecording();
                    break;

                case 101:
                    Toast.makeText(getApplicationContext(), "启动失败，第"+reStartNum+"", Toast.LENGTH_SHORT).show();
                    break;

                case 102:
                    Toast.makeText(getApplicationContext(), "重启失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case 103:
                    joinRoom();
                    anim();
                    break;

                case 104:
                    finish();
                    break;

                default:
                    break;
            }
        }
    };
    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_blue);
        setContentView(R.layout.activity_group_chat);
    }

    @Override
    protected void findViews() {
        llyt_back = (LinearLayout)findViewById(R.id.llyt_back);
        llyt_language = (LinearLayout)findViewById(R.id.llyt_language);
        mListView = (ListView)findViewById(R.id.listView);
        tv_tip = (TextView)findViewById(R.id.tv_chat_tip);
        tv_room_id = (TextView)findViewById(R.id.tv_room_id);
        iv_mic = (ImageView)findViewById(R.id.iv_mic);
        tv_language = (TextView)findViewById(R.id.tv_language);

        initView();

        llyt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llyt_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinGroupChatActivity.this, LanguageListActivity.class);
                startActivityForResult(intent, 101);
            }
        });
    }

    private void initView(){
        list = new ArrayList<>();
//        ChatInfo chatInfo = new ChatInfo();
//        chatInfo.setContent("你好，很高兴认识你.");
//        chatInfo.setType(1);
//        ChatInfo chatInfo2 = new ChatInfo();
//        chatInfo2.setContent("你好，很高兴认识你.我们一起去开黑啊，我们一起上波分。艾欧尼亚");
//        chatInfo2.setType(2);
//        list.add(chatInfo);
//        list.add(chatInfo2);

        if(list!=null && list.size()>0){
            mAdapter = new GroupChatListAdapter(list, JoinGroupChatActivity.this);
            mListView.setAdapter(mAdapter);
            mListView.setVisibility(View.VISIBLE);
            tv_tip.setVisibility(View.GONE);
        }else{
            mListView.setVisibility(View.GONE);
            tv_tip.setVisibility(View.VISIBLE);
        }

        userId = new UserController().getUserId();
        languages = getResources().getStringArray(R.array.languages_zh);
        languageCodes = getResources().getStringArray(R.array.languages);
        language = languages[0];
        lanCode = languageCodes[0];
        tv_language.setText(language);
        tv_room_id.setText("ID："+getIntent().getStringExtra("roomId"));
        initTranslatorCloud();
        mHandler.sendEmptyMessageDelayed(103, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && data!=null){
            language = data.getStringExtra("language");
            lanCode = data.getStringExtra("code");
            tv_language.setText(language);
            changeLanguage();
        }
    }

    private void initTranslatorCloud(){
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // 如果消息来自子线程
                if (msg.what == 0x123) {
                    // 将读取的内容追加显示在文本框中
                    String str = msg.obj.toString();
                    try{
                        Log.i( "wl", "消息"+str );
                        ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(str.getBytes());
                        parse( tInputStringStream );
                    }catch ( Exception e )
                    {
                        Log.i( "wl", "err node:" + e.toString() );
                    }
                }
            }
        };

        clientThread = new ControlThread(handler);

        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        new Thread(clientThread).start();
    }

    public void parse(InputStream xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  //取得DocumentBuilderFactory实例
        DocumentBuilder builder = factory.newDocumentBuilder(); //从factory获取DocumentBuilder实例
        Document doc = builder.parse(xml);   //解析输入流 得到Document实例
        Element rootElement = doc.getDocumentElement();

        NodeList items = rootElement.getElementsByTagName("description");
        if (items.getLength() != 0) {
            Node item = items.item(0);
            String str = item.getTextContent();
            start_record();
//            mTextView.append("\n\n启动:" + str + "\n");
            if (command_type == 2) {
                items = rootElement.getElementsByTagName("code");
                if (items.getLength() != 0) {
                    item = items.item(0);
                    str = item.getTextContent();
                    if("501".equals(str)){
                        tv_tip.setText("该房间不存在，2S后即将退出");
                        mHandler.sendEmptyMessageDelayed(104, 2000);
                    }
//                    mRoomIdTextView.setText( str );
                }
            }
            return;
        }
        Node item;
        String str;
        NodeList properties;
        Node property;
        switch (command_type) {
            case 1:
                items = rootElement.getElementsByTagName("asr");
                item = items.item(0);
                str= item.getTextContent();
                //mTextView.append("\n\n识别结果:" + str );

                items = rootElement.getElementsByTagName("translate");
                item = items.item(0);
                properties = item.getChildNodes();
                property = properties.item(1);
                if (property != null) {
                    String t = property.getTextContent();
//                    mTextView.append("\n翻译结果(" + property.getNodeName() + "):" + t);
                    String language = property.getNodeName();
                    ChatInfo chatInfo = new ChatInfo();
                    chatInfo.setContent(property.getTextContent());
                    chatInfo.setType(2);
                    list.add(chatInfo);
//                    if (language.equals("en")) {
////                        mSpeech.setLanguage(Locale.ENGLISH);
//                    } else if (language.equals("zh-CHS")) {
////                        mSpeech.setLanguage(Locale.CHINESE);
//                    }
                    //mSpeech.speak( property.getTextContent(), TextToSpeech.QUEUE_FLUSH, null);

                } else {
//                    mTextView.append("\n\n识别结果:" + str );
                    ChatInfo chatInfo = new ChatInfo();
                    chatInfo.setContent(str);
                    chatInfo.setType(1);
                    list.add(chatInfo);
                }
                if(mAdapter == null){
                    mAdapter = new GroupChatListAdapter(list, JoinGroupChatActivity.this);
                    mListView.setAdapter(mAdapter);
                    mListView.setVisibility(View.VISIBLE);
                    tv_tip.setVisibility(View.GONE);
                }else{
                    mAdapter.notifyDataSetChanged();
                }
                mListView.setSelection(list.size()-1);
                break;
            case 2:
            case 3:
                String id = userId;
                items = rootElement.getElementsByTagName("anchor_id");
                item = items.item(0);
                str = item.getTextContent();
//                mTextView.append( "\n" + str );

                items = rootElement.getElementsByTagName("translate");
                item = items.item(0);
                properties = item.getChildNodes();
                property = properties.item(1);
                String contentStr = property.getTextContent();
//                mTextView.append( str );

                UserController controller = new UserController();
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setContent(contentStr);
                if(str.equals(controller.getUserId())){
                    chatInfo.setType(controller.getHead());
                }else{
                    chatInfo.setType(new Random().nextInt(8));
                }
                list.add(chatInfo);

                if(mAdapter == null){
                    mAdapter = new GroupChatListAdapter(list, JoinGroupChatActivity.this);
                    mListView.setAdapter(mAdapter);
                    mListView.setVisibility(View.VISIBLE);
                    tv_tip.setVisibility(View.GONE);
                }else{
                    mAdapter.notifyDataSetChanged();
                }
                mListView.setSelection(list.size()-1);
                break;
        }
    }

    protected void start_record()
    {
        if(isRecording){
            return;
        }
        try {
            isRecording=true;
            if (mAudioRunnable != null) {
                mAudioRunnable.stop();
                mAudioRunnable = null;
            }
            if (mProcessSpeexRunnable != null) {
                mProcessSpeexRunnable.stop();
                mProcessSpeexRunnable = null;
            }

            if (mRTMPRunnable != null) {
                mRTMPRunnable.stop();
                mRTMPRunnable = null;
            }

            LinkedBlockingQueue<AudioRawData> blockingDeque = new LinkedBlockingQueue<>();
            // File rootDir = Environment.getExternalStorageDirectory();

            mRTMPRunnable = new RTMPRunnable( this, userId);
            mProcessSpeexRunnable = new ProcessSpeexRunnable(blockingDeque, this);
            mAudioRunnable = new AudioRecorderRunnable(blockingDeque, 1.5f, this);

            new Thread(mRTMPRunnable).start();
            new Thread(mProcessSpeexRunnable).start();
            new Thread(mAudioRunnable).start();

        } catch (Exception e) {
            Log.d("wl", "录音异常："+e.toString());
        }
    }

//    /**
//     * 开始录音
//     */
//    protected void start() {
//        try {
//            // 当用户按下按钮之后，将用户输入的数据封装成Message
//            // 然后发送给子线程Handler
//            Message msg = new Message();
//            msg.what = 0x345;
//            String languageIn = leftLanCode;
//            String languageOut = rightLanCode;
//
//            String str = "<root>\n  <command>start</command>\n  <anchor_id>" + userId + "</anchor_id>\n   <language_in>" + languageIn + "</language_in>\n  <language_out>" + languageOut + "</language_out>\n  <start_time>00:00:00</start_time>\n</root>";
//            Log.i( "wl", str );
//            byte buf[] = new byte[256];
//            buf[0] = '&';
//            int l = str.length()+3;
//            buf[1] = (byte)(l&0xff);
//            buf[2] = (byte)(l>>8&0xff);
//            System.arraycopy( str.getBytes(), 0, buf, 3, l-3 );
//            msg.obj = buf;
//            clientThread.revHandler.sendMessage(msg);
//        } catch (Exception e) {
//            Log.d("wl", "-----start-------"+e.toString());
//        }
//    }

    protected void joinRoom()
    {
        try {
            // 当用户按下按钮之后，将用户输入的数据封装成Message
            // 然后发送给子线程Handler
            Message msg = new Message();
            msg.what = 0x345;
            String languageIn = lanCode;
            String roomID = getIntent().getStringExtra("roomId");

            //String str = "<root>\n  <command>join room</command>\n  <id>" + id + "</id>\n   <language>" + languageIn + "</language>\n  <room_id>" + roomID + "</room_id>\n</root>";
            String str = "<root>\n  <command>join room</command>\n  <id>" + userId + "</id>\n   <language>" + languageIn + "</language>\n  <room_id>" + roomID + "</room_id>\n</root>";
            Log.i( "wl", str );
            byte buf[] = new byte[256];
            buf[0] = '&';
            int l = str.length()+3;
            buf[1] = (byte)(l&0xff);
            buf[2] = (byte)(l>>8&0xff);
            System.arraycopy( str.getBytes(), 0, buf, 3, l-3 );
            msg.obj = buf;
            clientThread.revHandler.sendMessage(msg);
        } catch (Exception e) {

        }
    }

    protected void leaveRoom()
    {

        if (isRecording) {
            if (mAudioRunnable != null) {
                mAudioRunnable.stop();
                mAudioRunnable = null;
            }
            if (mProcessSpeexRunnable != null) {
                mProcessSpeexRunnable.stop();
                mProcessSpeexRunnable = null;
            }
            isRecording = false;
        }
    }


    /**
     * 切换语言
     */
    protected void changeLanguage() {
        try {
            // 当用户按下按钮之后，将用户输入的数据封装成Message
            // 然后发送给子线程Handler
            Message msg = new Message();
            msg.what = 0x345;
            String languageIn = lanCode;
            String languageOut = "";

            String str = "<root>\n  <command>change language</command>\n  <anchor_id>" + userId + "</anchor_id>\n   <language_in>" + languageIn + "</language_in>\n  <language_out>" + languageOut + "</language_out>\n </root>";
            Log.i( "wl", str );
            byte buf[] = new byte[256];
            buf[0] = '&';
            int l = str.length()+3;
            buf[1] = (byte)(l&0xff);
            buf[2] = (byte)(l>>8&0xff);
            System.arraycopy( str.getBytes(), 0, buf, 3, l-3 );
            msg.obj = buf;
            clientThread.revHandler.sendMessage(msg);
        } catch (Exception e) {
            Log.d("wl", "-----start-------"+e.toString());
        }
//        stopRecording();
//        start();
    }

    /**
     * 录音结束
     */
    protected void stopRecording() {
        if (isRecording) {
            if (mAudioRunnable != null) {
                mAudioRunnable.stop();
                mAudioRunnable = null;
            }
            if (mProcessSpeexRunnable != null) {
                mProcessSpeexRunnable.stop();
                mProcessSpeexRunnable = null;
            }
            isRecording=false;
        }
    }

    @Override
    public void onPreProcess(short[] notProcessData, int len) {

    }

    @Override
    public void onProcess(byte[] data, int len) {
        mRTMPRunnable.putData(data, len);
    }

    @Override
    public void onProcessFinish(List<byte[]> data) {
        mCurrentRecordData = data;
        mRTMPRunnable.stop();
        Log.d("wl", "finish process speex data frames: " + data.size());
    }

    @Override
    public void onSendFinished() {

    }

    @Override
    protected void onDestroy() {
        stopRecording();
        iv_mic.clearAnimation();
        clientThread.closeSocket();
        mHandler.removeMessages(104);
        super.onDestroy();
    }

    private void anim(){
        iv_mic.startAnimation(AnimationUtils.loadAnimation(
                JoinGroupChatActivity.this, R.anim.fab_anim));
    }
}
