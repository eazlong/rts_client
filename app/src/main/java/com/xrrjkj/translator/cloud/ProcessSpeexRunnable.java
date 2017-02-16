package com.xrrjkj.translator.cloud;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by legendmohe on 15/11/13.
 */
public class ProcessSpeexRunnable implements Runnable {
    private static final String TAG = "ProcessSpeexRunnable";
    private final WeakReference<ProcessSpeexListener> mListener;

    private Speex mSpeexLib;
    private LinkedBlockingQueue<AudioRawData> mBufferQueue;
    private LinkedList<byte[]> mEncodedData;
    private boolean mStopped = false;
    private static final AudioRawData sStopAudioData = new AudioRawData(null, -1);

    private int totByte;

    ProcessSpeexRunnable(LinkedBlockingQueue<AudioRawData> queue, ProcessSpeexListener listener) {
        Log.d(TAG, "speex thread init");
        this.mBufferQueue = queue;
        this.mListener = new WeakReference<ProcessSpeexListener>(listener);
        init();
    }

    @Override
    public void run() {
        Log.d(TAG, "speex thread runing");
        if (this.mStopped || mSpeexLib == null) {
            Log.w(TAG, "ProcessSpeexRunnable not runnable.");
            return;
        }
        try {
            while (!this.mStopped) {
                AudioRawData data = null;
                //Log.d(TAG, "process start " );
                try {
                    data = this.mBufferQueue.take();
                } catch (InterruptedException e) {
                    Log.e(TAG, "poll error: " + e.toString());
                    continue;
                }
                if (data == null) {
                    continue;
                }else if (data == sStopAudioData) {
                    this.mStopped = true;
                    continue;
                }else if (data.len > 0)
                    process(data.data, data.len);
            }
        } finally {
            if (mListener.get() != null) {
                mListener.get().onProcessFinish(mEncodedData);
            }

            this.clean();
            Log.d(TAG, "thread exit.");
        }
    }

    public void stop() {
        if (this.mBufferQueue == null) {
            this.mStopped = true;
        }else {
            try {
                this.mBufferQueue.put(sStopAudioData);
            } catch (InterruptedException e) {
                Log.e(TAG, "stop flag error: " + e.toString());
            }
        }
    }

    private void init() {
        mSpeexLib = new Speex();
        mSpeexLib.init();

        mEncodedData = new LinkedList<>();
    }

    private void clean() {
        if (mSpeexLib != null) {
            mSpeexLib.close();
            mSpeexLib = null;
        }
    }

    private void process(short[] buffer, int n) {
        if (mListener.get() != null) {
            mListener.get().onPreProcess(buffer, n);
        }

        byte[] encoded = new byte[n];
        int encLen = this.mSpeexLib.encode(buffer, 0, encoded, buffer.length);

        totByte += encLen;
        //Log.d(TAG, "process speex from " + buffer.length/2 + " byte to " + totByte + " byte");
        if (mEncodedData != null) {
            byte[] wrapData = new byte[encLen];
            System.arraycopy(encoded, 0, wrapData, 0, wrapData.length);
            mEncodedData.add(wrapData);
        }

        if (mListener.get() != null) {
            mListener.get().onProcess(encoded, encLen);
        }
    }

    public interface ProcessSpeexListener {
        void onPreProcess(short[] notProcessData, int len);
        void onProcess(byte[] data, int len);
        void onProcessFinish(List<byte[]> data);
    }
}
