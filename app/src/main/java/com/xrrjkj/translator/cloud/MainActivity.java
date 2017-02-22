package com.xrrjkj.translator.cloud;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends AppCompatActivity
        implements ProcessSpeexRunnable.ProcessSpeexListener , RTMPRunnable.RTMPListener{

    private static final String TAG = "MainActivity";

    private static final int STOP_RECORDING = 0;

    private TextView mTextView;
    private TextView mRoomIdTextView;
    private EditText mIDEditText;
    private Spinner mInLanguageSpinner;
    private Spinner mOutLanguageSpinner;
    private Button btn_RecordStart;
    private Button btn_RecordStop;
    private Button btn_Clear;
    private Button btn_Create;
    private Button btn_Join;
    private Button btn_Leave;
    private Button btn_Single;
    private Button btn_Multi;

    private ArrayAdapter languageAdapter;
    private int command_type;

    private boolean isRecording;
    List<byte[]> mCurrentRecordData = null;
    private MyHandler mHandler;
    private TextToSpeech mSpeech = null;

    private AudioRecorderRunnable mAudioRunnable;
    private ProcessSpeexRunnable mProcessSpeexRunnable;
    private RTMPRunnable mRTMPRunnable;
/*
    private OkHttpClient mHttpClient = new OkHttpClient();
    private MediaType mMediaType = MediaType.parse("text/html; charset=UTF-8");
*/

    Handler handler;

    ControlThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView( R.layout.content_scrolling);

        mTextView = (TextView) this.findViewById(R.id.jni_text_view);
        mTextView.setText("新睿软件科技");
        mRoomIdTextView = (TextView) this.findViewById(R.id.room);

        btn_RecordStart = (Button) findViewById(R.id.btn_RecordStart);
        btn_RecordStop = (Button) findViewById(R.id.btn_RecordStop);
        btn_RecordStop.setEnabled(false);
        btn_Clear = (Button)findViewById(R.id.btn_Clear );
        btn_Create = (Button)findViewById(R.id.btn_Create );
        btn_Join = (Button)findViewById(R.id.btn_Join );
        btn_Leave = (Button)findViewById(R.id.btn_Leave );
        btn_Single = (Button)findViewById(R.id.btn_Single );
        btn_Multi = (Button)findViewById(R.id.btn_Multi );

        mIDEditText = (EditText)this.findViewById(R.id.id_edit );
        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        mIDEditText.setText( szImei );

        mInLanguageSpinner = (Spinner)findViewById(R.id.in_language_spinner );
        mOutLanguageSpinner = (Spinner)findViewById(R.id.out_language_spinner );

        languageAdapter = ArrayAdapter.createFromResource( this,R.array.languages, android.R.layout.simple_spinner_item );
        languageAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        mInLanguageSpinner.setAdapter( languageAdapter );
        mOutLanguageSpinner.setAdapter( languageAdapter );
        mOutLanguageSpinner.setSelection(1);
        btn_Create.setVisibility( View.INVISIBLE );
        btn_Join.setVisibility( View.INVISIBLE );
        btn_Leave.setVisibility( View.INVISIBLE );

        btn_RecordStart.setOnClickListener(click);
        btn_RecordStop.setOnClickListener(click);
        btn_Clear.setOnClickListener( click );
        btn_Create.setOnClickListener( click );
        btn_Join.setOnClickListener( click );
        btn_Leave.setOnClickListener( click );
        btn_Single.setOnClickListener( click );
        btn_Multi.setOnClickListener( click );

        mTextView.setMovementMethod(new ScrollingMovementMethod());

        mSpeech = new TextToSpeech(MainActivity.this, new TTSListener());

        mHandler = new MyHandler();

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // 如果消息来自子线程
                if (msg.what == 0x123) {
                    // 将读取的内容追加显示在文本框中
                    String str = msg.obj.toString();
                    try{
                        Log.i( TAG, str );
                        ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(str.getBytes());
                        parse( tInputStringStream );
                    }catch ( Exception e )
                    {
                        Log.i( TAG, "err node:" + e.toString() );
                    }
                }
            }
        };

        clientThread = new ControlThread(handler);

        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        new Thread(clientThread).start();
    }
    private class TTSListener implements TextToSpeech.OnInitListener {

        @Override
        public void onInit(int status) {
            // TODO Auto-generated method stub
            if (status == TextToSpeech.SUCCESS) {
                //int result = mSpeech.setLanguage(Locale.ENGLISH);
//                int result = SetLanguage(curLang);
//                //如果打印为-2，说明不支持这种语言
//                Toast.makeText(MainActivity.this, "-------------result = " + result, Toast.LENGTH_LONG).show();
//                if (result == TextToSpeech.LANG_MISSING_DATA
//                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    System.out.println("-------------not use");
//                } else {
//                    mSpeech.speak("i love you", TextToSpeech.QUEUE_FLUSH, null);
//                }
            }
        }

    }
    public void parse(InputStream xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  //取得DocumentBuilderFactory实例
        DocumentBuilder builder = factory.newDocumentBuilder(); //从factory获取DocumentBuilder实例
        Document doc = builder.parse(xml);   //解析输入流 得到Document实例
        Element rootElement = doc.getDocumentElement();

        NodeList items = rootElement.getElementsByTagName("description");
        if ( items.getLength() != 0 ) {
            Node item = items.item(0);
            String str = item.getTextContent();
            mTextView.append("\n\n启动:" + str + "\n");
            if ( command_type == 2 )
            {
                items = rootElement.getElementsByTagName("room_id");
                if ( items.getLength() != 0 )
                {
                    item = items.item(0);
                    str = item.getTextContent();
                    mRoomIdTextView.setText( str );
                }
            }
            return;
        }

        switch ( command_type )
        {
            case 1:
            {
                items = rootElement.getElementsByTagName("asr");
                Node item = items.item(0);
                String str = item.getTextContent();
                //mTextView.append("\n\n识别结果:" + str );

                items = rootElement.getElementsByTagName( "translate" );
                item = items.item(0);
                NodeList properties = item.getChildNodes();
                Node property = properties.item(1);
                if ( property != null ) {
                    String t = property.getTextContent();
                    mTextView.append("\n翻译结果(" + property.getNodeName() + "):" + t);
                    String language = property.getNodeName();
                    if ( language.equals( "en" ) )
                    {
                        mSpeech.setLanguage(Locale.ENGLISH);
                    }
                    else if ( language.equals( "zh-CHS" ) )
                    {
                        mSpeech.setLanguage(Locale.CHINESE);
                    }
                    //mSpeech.speak( property.getTextContent(), TextToSpeech.QUEUE_FLUSH, null);

                }else{
                    mTextView.append("\n\n识别结果:" + str );
                }

            }
            break;
            case 2:
            case 3:
            {
                String id = mIDEditText.getText().toString();
                items = rootElement.getElementsByTagName("anchor_id");
                Node item = items.item(0);
                String str = item.getTextContent();
                str += ":";
                mTextView.append( "\n" + str );

                items = rootElement.getElementsByTagName( "translate" );
                item = items.item(0);
                NodeList properties = item.getChildNodes();
                Node property = properties.item(1);
                str = property.getTextContent();
                mTextView.append( str );
            }
            break;
        }
        Log.i( TAG, "height:"+mTextView.getLineHeight()+":"+mTextView.getLineCount()+":"+mTextView.getHeight() );
        if ( mTextView.getLineCount()*mTextView.getLineHeight() > mTextView.getHeight() )
        {
            mTextView.scrollTo( 0, mTextView.getLineCount()*mTextView.getLineHeight()-mTextView.getHeight()/2 );
        }
    }
    private View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_RecordStart:
                    start();
                    command_type = 1;
                    break;
                case R.id.btn_RecordStop:
                    mHandler.sendEmptyMessageDelayed(STOP_RECORDING, 500);
                    break;
                case R.id.btn_Clear:
                    mTextView.setText("");
                    break;
                case R.id.btn_Create:
                    mTextView.setText("");
                    createRoom();
                    command_type = 2;
                    break;
                case R.id.btn_Join:
                    mTextView.setText( "" );
                    joinRoom();
                    command_type = 3;
                    break;
                case R.id.btn_Leave:
                    leaveRoom();
                    break;
                case R.id.btn_Single:
                    mOutLanguageSpinner.setVisibility(View.VISIBLE);
                    btn_RecordStart.setVisibility( View.VISIBLE );
                    btn_RecordStop.setVisibility( View.VISIBLE );
                    btn_Clear.setVisibility( View.VISIBLE );
                    btn_Create.setVisibility( View.INVISIBLE );
                    btn_Join.setVisibility( View.INVISIBLE );
                    btn_Leave.setVisibility( View.INVISIBLE );
                    mRoomIdTextView.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_Multi:
                    mOutLanguageSpinner.setVisibility(View.INVISIBLE);
                    btn_RecordStart.setVisibility( View.INVISIBLE );
                    btn_RecordStop.setVisibility( View.INVISIBLE );
                    btn_Clear.setVisibility( View.INVISIBLE );
                    btn_Create.setVisibility( View.VISIBLE );
                    btn_Join.setVisibility( View.VISIBLE );
                    btn_Leave.setVisibility( View.VISIBLE );
                    mRoomIdTextView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    protected void createRoom()
    {
        try {
            // 当用户按下按钮之后，将用户输入的数据封装成Message
            // 然后发送给子线程Handler
            Message msg = new Message();
            msg.what = 0x345;
            String id = mIDEditText.getText().toString();
            String languageIn = mInLanguageSpinner.getSelectedItem().toString();
            String str = "<root>\n  <command>create room</command>\n  <id>" + id + "</id>\n   <language>" + languageIn + "</language>\n</root>";
            Log.i( TAG, str );
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

        start_record();

        btn_Join.setEnabled( false );
        btn_Create.setEnabled( false );
        btn_Leave.setEnabled( true );
    }

    protected void joinRoom()
    {
        try {
            // 当用户按下按钮之后，将用户输入的数据封装成Message
            // 然后发送给子线程Handler
            Message msg = new Message();
            msg.what = 0x345;
            String id = mIDEditText.getText().toString();
            String languageIn = mInLanguageSpinner.getSelectedItem().toString();
            String roomID = mRoomIdTextView.getText().toString();

            //String str = "<root>\n  <command>join room</command>\n  <id>" + id + "</id>\n   <language>" + languageIn + "</language>\n  <room_id>" + roomID + "</room_id>\n</root>";
            String str = "<root>\n  <command>join room</command>\n  <id>" + id + "</id>\n   <language>" + languageIn + "</language>\n  <room_id>" + roomID + "</room_id>\n</root>";
            Log.i( TAG, str );
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

        start_record();

        btn_Join.setEnabled( false );
        btn_Create.setEnabled( false );
        btn_Leave.setEnabled( true );
    }

    protected void leaveRoom()
    {
        btn_Join.setEnabled( true );
        btn_Create.setEnabled( true  );
        btn_Leave.setEnabled( false );

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
            Toast.makeText(MainActivity.this, "录音结束", Toast.LENGTH_SHORT).show();
        }
    }

    protected void start_record()
    {
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

            mRTMPRunnable = new RTMPRunnable( this, mIDEditText.getText().toString() );
            mProcessSpeexRunnable = new ProcessSpeexRunnable(blockingDeque, this);
            mAudioRunnable = new AudioRecorderRunnable(blockingDeque, 1.5f, this);

            new Thread(mRTMPRunnable).start();
            new Thread(mProcessSpeexRunnable).start();
            new Thread(mAudioRunnable).start();

            Toast.makeText(MainActivity.this, "开始录音", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 开始录音
     */
    protected void start() {
        start_record();

        try {
            // 当用户按下按钮之后，将用户输入的数据封装成Message
            // 然后发送给子线程Handler
            Message msg = new Message();
            msg.what = 0x345;
            String id = mIDEditText.getText().toString();
            String languageIn = mInLanguageSpinner.getSelectedItem().toString();
            String languageOut = mOutLanguageSpinner.getSelectedItem().toString();

            String str = "<root>\n  <command>start</command>\n  <anchor_id>" + id + "</anchor_id>\n   <language_in>" + languageIn + "</language_in>\n  <language_out>" + languageOut + "</language_out>\n  <start_time>00:00:00</start_time>\n</root>";
            Log.i( TAG, str );
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

        btn_RecordStart.setEnabled(false);
        btn_RecordStop.setEnabled(true);
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

            btn_RecordStop.setEnabled(false);
            btn_RecordStart.setEnabled(true);
            Toast.makeText(MainActivity.this, "录音结束", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (isRecording) {
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
        }
        super.onDestroy();
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
        Log.d(TAG, "finish process speex data frames: " + data.size());
    }
/*
    private Request generateRequest(String payload, String type) {
        String url = "http://119.29.102.249:8888/mqtt_base64?t=12345678&type=" + type;
        RequestBody body = RequestBody.create(mMediaType, payload);
        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }

    private void sendMessageRequest(Request request) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText("sending request");
            }
        });
        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText("http error");
                        btn_RecordStart.setEnabled(true);
                    }
                });
            }

            @Override public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                final String str = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(str);
                        btn_RecordStart.setEnabled(true);
                    }
                });
            }
        });
    }

    private void playRecord() {
        btn_RecordPlay.setEnabled(false);
        if (mCurrentRecordData == null || mCurrentRecordData.size() == 0)
            return;

        int bufferSizeInBytes = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 2 * bufferSizeInBytes, AudioTrack.MODE_STREAM);
        audioTrack.play();

        Speex speex = new Speex();
        speex.init();

        int maxFrameSize = speex.getFrameSize();
        for (byte[] bytes: mCurrentRecordData) {
            short[] decData = new short[maxFrameSize];
            int dec = speex.decode(bytes, decData, bytes.length);
            if (dec > 0) {
                audioTrack.write(decData, 0, dec);
            }
        }
        audioTrack.stop();
        audioTrack.release();
        speex.close();
        btn_RecordPlay.setEnabled(true);
    }
*/
    @Override
    public void onSendFinished() {
//        String payload = FileUtils.FileToBase64(file);
//        Log.d(TAG, "base 64:" + payload);
//        Log.d(TAG, "file path:" + file.getAbsolutePath());
//        Request request = generateRequest(payload, "message");
//        sendMessageRequest(request);
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_RECORDING:
                    stopRecording();
                    break;
                default:
                    break;
            }
        }
    }
}

