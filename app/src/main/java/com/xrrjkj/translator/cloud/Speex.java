package com.xrrjkj.translator.cloud;

/**
 * Created by legendmohe on 15/11/9.
 */
public class Speex {
    /* quality
    * 1 : 4kbps (very noticeable artifacts, usually intelligible)
    * 2 : 6kbps (very noticeable artifacts, good intelligibility)
    * 4 : 8kbps (noticeable artifacts sometimes)
    * 6 : 11kpbs (artifacts usually only noticeable with headphones)
    * 8 : 15kbps (artifacts not usually noticeable)
    */
    private static final int DEFAULT_COMPRESSION = 8;

    Speex() {
    }

    public void init() {
        load();
        open(DEFAULT_COMPRESSION);
    }

    private void load() {
        try {
            System.loadLibrary("speex");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static native int open(int compression);
    public static native int getFrameSize();
    public static native int decode(byte encoded[], short lin[], int size);
    public static native int encode(short lin[], int offset, byte encoded[], int size);
    public static native void close();
}
