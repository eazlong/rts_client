package com.xrrjkj.translator.cloud.speex;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/8/8.
 */
public class FileWriter {
    //写数据到SD中的文件
    public void writeFileSdcardFile(String fileName,String write_str) throws IOException {
        try{

            FileOutputStream fout = new FileOutputStream(fileName);
            byte [] bytes = write_str.getBytes();

            fout.write(bytes);
            fout.close();
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }


    //读SD中的文件
    public String readFileSdcardFile(String fileName) throws IOException{
        String res="";
        try{
            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte [] buffer = new byte[length];
            fin.read(buffer);

            //res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();
        }

        catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
