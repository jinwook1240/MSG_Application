package com.example.msg_application.subtitle;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.msg_application.service.BTCTemplateService;
import com.example.msg_application.utils.AppSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 욱 on 2016-10-28.
 */

public class SenderClass extends Thread{
    BTCTemplateService mService;
    String SdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    long Synctime;
    String filename;
    String file;
    String filediv[];
    int prevtime;

    public SenderClass(String filename){
        Synctime  = System.currentTimeMillis();//Synctime-System.currentTimeMillis() : 시간차
        this.filename = filename;
        this.file = readfile(SdcardPath+"/"+filename);
        Log.v("parsed maxnum",file);
        filediv = file.replaceAll("\n","").replaceAll("\r","").split("SYNC");//sync태그 찾기/나누기
        Log.v("parsed maxnum",""+filediv.length);
    }
    public void run(){
        sendMessage("Started!!!");
        for(int i=0;i<=filediv.length;i++){
            Log.i("parsing smi", filediv[i]);
            StringBuffer messagem = new StringBuffer("");
            messagem.delete(0,messagem.length());
            Pattern Sync = Pattern.compile("Start", Pattern.CASE_INSENSITIVE);//start찾기
            int time;
            int endoftag;
            while(true){
                Matcher mat=Sync.matcher(filediv[i]);
                endoftag = filediv[i].indexOf('>');//start 끝 찾기
                if(mat.find()){
                    int k=Integer.parseInt(filediv[i].substring(mat.end()+1,endoftag));
                    time =k-prevtime;
                    prevtime = k;
                    break;
                }
                else{
                    i++;
                }}
            try {
                Log.v("tagstart,end",(endoftag+12)+filediv[i].length()+"");
                messagem.append(filediv[i].substring(endoftag+12,filediv[i].length()));
                Log.e("parsing smi", "asdf"+(time-(Synctime-System.currentTimeMillis())));
                sleep((time-(Synctime-System.currentTimeMillis()))/10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendMessage(messagem.toString());
        }
    }
    /*@Override
    public void run() {
        String file = readfile(filename);
        StringBuffer message = new StringBuffer("");
        String div[]=file.split("\n");
        int j=1;
        for(int i=0; i<div.length; i++){
            boolean b = true;
            Pattern arrow = Pattern.compile("-->", Pattern.CASE_INSENSITIVE);
            Log.v("이번 자막은", j+" 번째 자막입니다.");
            if(!(Integer.parseInt(div[i])==j||div[i].equals(j+"")))
            while(b){
                j++;
                try {
                    wait(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
            sendMessage(message.toString());
        }

    }*/
    public String readfile(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            Log.v("filecheck", filename + "not exist");
        }

        StringBuffer sb = new StringBuffer();
        Log.v("fileWriter", filename + " reading started");
        try {// 저장일시 읽어들이기
            FileInputStream fis = new FileInputStream(filename);
            int n;
            while ((n = fis.available()) > 0) {
                byte b[] = new byte[n];
                if (fis.read(b) == -1)
                    break;
                sb.append(new String(b));
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Could not find file" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    private void sendMessage(String message) {
        if(mService != null && message != null)
            mService.sendMessageToRemote(message);
        //mEditChat.setText("");
    }
}