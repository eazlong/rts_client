package com.xrrjkj.translator.cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by Administrator on 2016/7/19.
 */
public class ControlThread implements Runnable {
    private final static String TAG = "ControlThread";

    private Socket s;
    // 定义向UI线程发送消息的Handler对象
    Handler handler;
    // 定义接收UI线程的Handler对象
    public Handler revHandler;
    // 该线程处理Socket所对用的输入输出流
    BufferedReader br = null;
    OutputStream os = null;

    public ControlThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        s = new Socket();
        try {
            s.connect( new InetSocketAddress("120.24.44.224", 1936), 5000 );
            br = new BufferedReader( new InputStreamReader(s.getInputStream()) );
            os = s.getOutputStream();
            // 启动一条子线程来读取服务器相应的数据
            new Thread() {

                @Override
                public void run() {
                    Log.d( TAG, "thread read start!" );
                    int len = 0;
                    char buf[] = new char[1024];
                    // 不断的读取Socket输入流的内容
                    try {
                        while ((len = br.read( buf, 0, 1024 )) != 0 ) {
                            Log.d( TAG, "thread read start! " + len );
                            String str = new String(buf);
                            str = str.substring( 0, len-1 );
                            // 每当读取到来自服务器的数据之后，发送的消息通知程序
                            // 界面显示该数据

                            Message msg = new Message();
                            msg.what = 0x123;
                            msg.obj = str;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    Log.d( TAG, "thread read end!" );
                }

            }.start();
            // 为当前线程初始化Looper
            Looper.prepare();
            // 创建revHandler对象
            revHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    // 接收到UI线程的中用户输入的数据
                    Log.d( TAG, "process control message!"+msg.what );
                    if (msg.what == 0x345) {
                        // 将用户在文本框输入的内容写入网络
                        try {
                            os.write((byte[]) msg.obj);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            };
            // 启动Looper
            Looper.loop();

        } catch (SocketTimeoutException e) {
            Message msg = new Message();
            msg.what = 0x123;
            msg.obj = "网络连接超时！";
            handler.sendMessage(msg);
        } catch (IOException io) {
            Log.d( TAG, "thread read error!" );
            io.printStackTrace();
        }

    }

    public void closeSocket(){
        if(s !=null){
            try{
                s.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}