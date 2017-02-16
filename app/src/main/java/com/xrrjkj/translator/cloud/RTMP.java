package com.xrrjkj.translator.cloud;

/**
 * Created by Administrator on 2016/7/18.
 */
public class RTMP {
    RTMP() {
    }

    public void init( String id ) {
        load();
        initialize( "rtmp://120.24.44.224:1935 conn=O:1 conn=NS:anchorid:" + id + " conn=O:0" );
    }

    public void load() {
        try {
            System.loadLibrary("rtmp");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public native void initialize( String url );
    public native void send( byte buf[], int size );
    public native void stop();
}
