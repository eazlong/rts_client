package com.xrrjkj.translator.cloud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by legendmohe on 15/11/13.
 */
public class AudioRecorderRunnable implements Runnable {
    private static final String TAG = "AudioRecorderRunnable";

    private boolean mStopped = false;
    private LinkedBlockingQueue<AudioRawData> mBufferQueue;
    private float mGain;
    private boolean mSupportBlueTooth = false;
//    FileOutputStream fout;
//    private static String mFileName = null;
    private AudioManager mAudioManager = null;
    private Context mContext = null;


    AudioRecorderRunnable(LinkedBlockingQueue<AudioRawData> queue, float gain, Context context)  {
        this.mBufferQueue = queue;
        this.mGain = gain;
        this.mContext = context;
        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }
    private void startBluetooth() {
        if(!mAudioManager.isBluetoothScoAvailableOffCall()){
            Log.d(TAG, "系统不支持蓝牙录音");
            return;
        }
        //蓝牙录音的关键，启动SCO连接，耳机话筒才起作用
        mAudioManager.startBluetoothSco();
        //蓝牙SCO连接建立需要时间，连接建立后会发出ACTION_SCO_AUDIO_STATE_CHANGED消息，通过接收该消息而进入后续逻辑。
        //也有可能此时SCO已经建立，则不会收到上述消息，可以startBluetoothSco()前先stopBluetoothSco()
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                if ( AudioManager.SCO_AUDIO_STATE_CONNECTED == state ) {
                mAudioManager.setBluetoothScoOn(true);  //打开SCO
                mContext.unregisterReceiver(this);  //别遗漏
            }else{//等待一秒后再尝试启动SCO
                try {
                        Thread.sleep(1000);
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    mAudioManager.startBluetoothSco();
                }
            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
    }

    private void stopBluetooth() {
        if(mAudioManager.isBluetoothScoOn()){
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.stopBluetoothSco();
        }
    }
 /*   private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            if(!mAudioManager.isBluetoothA2dpOn()) mAudioManager.setBluetoothA2dpOn(true); //如果A2DP没建立，则建立A2DP连接
            mAudioManager.stopBluetoothSco();//如果SCO没有断开，由于SCO优先级高于A2DP，A2DP可能无声音
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mAudioManager.setStreamSolo(AudioManager.STREAM_MUSIC, true);
            //让声音路由到蓝牙A2DP。此方法虽已弃用，但就它比较直接、好用。
            mAudioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_BLUETOOTH_A2DP, AudioManager.ROUTE_BLUETOOTH);
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        mAudioManager.setStreamSolo(AudioManager.STREAM_MUSIC, false);
    }
*/
    @Override
    public void run() {
        if (this.mStopped) {
            Log.w(TAG, "ProcessRunnable is running.");
            return;
        }

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        AudioRecord recorder = null;
        short[][]   buffers  = new short[256][160];
        int         ix       = 0;
        float gain = this.mGain;
        try {
            int n = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    8000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    n*10);
//            recorder.setPositionNotificationPeriod(8000);
            startBluetooth();

            recorder.startRecording();
            while(!this.mStopped) {
                short[] buffer = buffers[ix++ % buffers.length];
                int numRead = recorder.read(buffer, 0, buffer.length);
                if (numRead > 0) {
                    for (int i = 0; i < numRead; ++i)
                        buffer[i] = (short) Math.min((int) (buffer[i] * gain), (int) Short.MAX_VALUE);
                }
                Log.i( TAG, "Record data:"+ n );
                this.mBufferQueue.offer(new AudioRawData(buffer, n));

            }
        } catch(Throwable x) {
            Log.w(TAG, "Error reading voice audio", x);
        } finally {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                stopBluetooth();
            }
            Log.d(TAG, "thread exit.");
        }
    }

    public void stop() {
        this.mStopped = true;
    }

    public void setSupportBlueTooth( boolean support )
    {
        this.mSupportBlueTooth = support;
    }
}
