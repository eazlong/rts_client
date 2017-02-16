package com.xrrjkj.translator.cloud;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.LinkedBlockingQueue;


public class RTMPRunnable implements Runnable {
    private final static String TAG = "RTMPRunnable";

    private RTMP rtmp = new RTMP();
    private volatile boolean mIsRecording;
    private ProcessedData mData;
    private LinkedBlockingQueue<ProcessedData> mDataQueue;
    private String id;
    private WeakReference<RTMPListener> rtmpListener;

    public static int sWritePackageSize = 1024;

    public RTMPRunnable( RTMPListener listener, String id ) {
        this.rtmpListener = new WeakReference<>(listener);
        mDataQueue = new LinkedBlockingQueue<>();
        this.id = id;
    }

    public void run() {
        Log.d(TAG, "write thread runing");

        rtmp.init( id );

        mIsRecording = true;
        while (this.isRecording()) {
            try {
                mData = mDataQueue.take();
            } catch (InterruptedException e) {
                Log.e(TAG, Log.getStackTraceString(e));
                mData = null;
            }
            if (mData == sStopFlagData) {
                mIsRecording = false;
            }else if (mData != null) {
                Log.d(TAG, "mData size=" + mData.size);
                //mSpeexWriteClient.writePacket(mData.processed, mData.size);
                rtmp.send( mData.processed, mData.size );
            }
        }
        rtmp.stop();

        if (rtmpListener.get() != null) {
            rtmpListener.get().onSendFinished();
        }
        Log.d(TAG, "write thread exit");
    }

    public boolean putData(final byte[] buf, int size) {
        ProcessedData data = new ProcessedData(buf, size);
        try {
            mDataQueue.put(data);
        } catch (InterruptedException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    public void stop() {
        try {
            mDataQueue.put(sStopFlagData);
        } catch (InterruptedException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public interface RTMPListener {
        void onSendFinished();
    }

    static class ProcessedData {
        ProcessedData(byte[] buf, int size) {
            if(buf != null)
                System.arraycopy(buf, 0, this.processed, 0, size);
            this.size = size;
        }
        private int size;
        private byte[] processed = new byte[sWritePackageSize];
    }
    
    private static final ProcessedData sStopFlagData = new ProcessedData(null, -1);
}
