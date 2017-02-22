package com.xrrjkj.translator.cloud.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xrrjkj.translator.cloud.AudioRecorderRunnable;
import com.xrrjkj.translator.cloud.ControlThread;
import com.xrrjkj.translator.cloud.ProcessSpeexRunnable;
import com.xrrjkj.translator.cloud.R;
import com.xrrjkj.translator.cloud.RTMPRunnable;
import com.xrrjkj.translator.cloud.adapter.GroupChatListAdapter;
import com.xrrjkj.translator.cloud.adapter.RoomListAdapter;
import com.xrrjkj.translator.cloud.bean.ChatInfo;
import com.xrrjkj.translator.cloud.bean.RoomInfo;
import com.xrrjkj.translator.cloud.function.UserController;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by user01 on 2016/12/30.
 */
public class RoomChooseListActivity extends BaseFragmentActivity implements ProcessSpeexRunnable.ProcessSpeexListener , RTMPRunnable.RTMPListener{

    private LinearLayout llyt_back;
    private RelativeLayout rlyt_add;
    private ImageView iv_add;
    private TextView tv_title;
    private EditText editText;

    private ListView mListView;
    private RoomListAdapter mAdapter;
    private List<RoomInfo> roomInfos;

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
                    break;

                case 101:
                    Toast.makeText(getApplicationContext(), "启动失败，第"+reStartNum+"", Toast.LENGTH_SHORT).show();
                    break;

                case 102:
                    Toast.makeText(getApplicationContext(), "重启失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case 103:
                    getRooms();
                    break;

                case 105:
                    String roomId = editText.getText().toString().trim();
                    Intent intent = new Intent(RoomChooseListActivity.this, JoinGroupChatActivity.class);
                    intent.putExtra("roomId", roomId);
                    startActivity(intent);
                    break;

                default:
                    break;
            }
        }
    };
    @Override
    protected void setContentView() {
        setWindowStatusBarColor(this, R.color.bg_blue);
        setContentView(R.layout.activity_join_group_chat);
    }

    @Override
    protected void findViews() {
        llyt_back = (LinearLayout)findViewById(R.id.llyt_back);
        rlyt_add = (RelativeLayout) findViewById(R.id.rlyt_add);
        iv_add = (ImageView)findViewById(R.id.iv_add);

        mListView = (ListView)findViewById(R.id.listView);
        initView();

        llyt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rlyt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.view_join_room, null);
                editText= (EditText)layout.findViewById(R.id.editText);
                final MaterialDialog alert = new MaterialDialog(RoomChooseListActivity.this).setContentView(layout);

                alert.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(RoomChooseListActivity.this.INPUT_METHOD_SERVICE);
                        //隐藏键盘
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        alert.dismiss();
                        mHandler.sendEmptyMessageDelayed(105, 500);

                    }
                })
                        .setNegativeButton(getString(R.string.cancel),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.dismiss();
                                    }
                                })
                        .setCanceledOnTouchOutside(true);

                alert.show();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RoomChooseListActivity.this, JoinGroupChatActivity.class);
                intent.putExtra("roomId", roomInfos.get(position).getRoomId());
                startActivity(intent);
            }
        });
    }

    private void initView(){
        roomInfos = new ArrayList<>();
//        RoomInfo roomInfo = new RoomInfo();
//        roomInfo.setDatetime("2016/10/20");
//        roomInfo.setPepNum(20);
//        roomInfo.setRoomId("123654");
//
//        RoomInfo roomInfo2 = new RoomInfo();
//        roomInfo2.setDatetime("2016/10/20");
//        roomInfo2.setPepNum(20);
//        roomInfo2.setRoomId("123654");
//        roomInfos.add(roomInfo);
//        roomInfos.add(roomInfo2);
        mAdapter = new RoomListAdapter(roomInfos, RoomChooseListActivity.this);
        mListView.setAdapter(mAdapter);

        userId = new UserController().getUserId();
        initTranslatorCloud();
        mHandler.sendEmptyMessageDelayed(103, 1000);
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
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        //获取XmlPullParser实例
        XmlPullParser pullParser = factory.newPullParser();
        pullParser.setInput(xml, "UTF-8");
        //开始
        int eventType = pullParser.getEventType();
        RoomInfo roomInfo = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String nodeName = pullParser.getName();
            switch (eventType) {
                //文档开始
                case XmlPullParser.START_DOCUMENT:
                    break;

                //开始节点
                case XmlPullParser.START_TAG:
                    if ("persons".equals(nodeName)) {
                        roomInfo= new RoomInfo();
                        roomInfo.setPepNum(Integer.parseInt(pullParser.nextText()));
                    } else if ("id".equals(nodeName) && roomInfo!=null) {
                        roomInfo.setRoomId(pullParser.nextText());
                    } else if ("create_time".equals(nodeName) && roomInfo!=null) {
                        roomInfo.setDatetime(pullParser.nextText());
                        roomInfos.add(roomInfo);
                    }
                    break;
                //结束节点
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            // 手动的触发下一个事件
            eventType = pullParser.next();
        }
        if(mAdapter == null){
            mAdapter = new RoomListAdapter(roomInfos, RoomChooseListActivity.this);
            mListView.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
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

    protected void getRooms()
    {
        try {
            // 当用户按下按钮之后，将用户输入的数据封装成Message
            // 然后发送给子线程Handler
            Message msg = new Message();
            msg.what = 0x345;
            String str = "<root>\n  <command>get room list</command>\n  <id>" + userId + "</id>\n </root>";
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
}
