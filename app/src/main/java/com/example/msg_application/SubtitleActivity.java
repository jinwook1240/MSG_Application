package com.example.msg_application;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.example.msg_application.subtitle.service.BTCTemplateService;
import com.google.zxing.client.android.integration.IntentIntegrator;
import com.google.zxing.client.android.integration.IntentResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 욱 on 2016-11-02.
 */

public class SubtitleActivity extends Activity
{

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    TextView req1 = (TextView) findViewById(R.id.requesttxt1 );
    TextView req2 = (TextView) findViewById(R.id.requesttxt2 );
    TextView bt = (TextView) findViewById(R.id.nextbutton1);




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread sender = new Timethread(Environment.getExternalStorageDirectory().getAbsolutePath()+"/subtitle");
    }

    class Timethread extends Thread {
        String SdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        long Synctime;
        String filename;
        String file;
        String filediv[];
        int prevtime;
        public Timethread(String filename){
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

        private IListener mListener =null;
        // Sends user message to remote
        private void sendMessage(String message1) {

            if(message1 == null || message1.length() < 1)
                return;
            // send to remote
            if(mListener != null)
                mListener.OnFragmentCallback(IListener.CALLBACK_SEND_MESSAGE, 0, 0, message1, null,null);
            else
                return;
            // show on text view
            if(req1 != null) {
                //mTextChat.append("\nSend: ");
                //mTextChat.append(message1);
                int scrollamout = req1.getLayout().getLineTop(req1.getLineCount()) - req1.getHeight();
                if (scrollamout > req1.getHeight())
                    bt.scrollTo(0, scrollamout);
            }
            //mEditChat.setText("");    주석이므로 낙서를 하여도 상관없다!
        }


    }








    BTCTemplateService mService;
    mActivityHandler = new ActivityHandler();


    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what) {
                // Receives BT state messages from service
                // and updates BT state UI
                case Constants.MESSAGE_BT_STATE_INITIALIZED:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_init));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_LISTENING:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_wait));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTING:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_connect));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_away));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTED:
                    if(mService != null) {
                        String deviceName = mService.getDeviceName();
                        if(deviceName != null) {
                            mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                                    getResources().getString(R.string.bt_state_connected) + " " + deviceName);
                            mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online));
                        }
                    }
                    break;
                case Constants.MESSAGE_BT_STATE_ERROR:
                    mTextStatus.setText(getResources().getString(R.string.bt_state_error));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
                    break;

                // BT Command status
                case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:
                    mTextStatus.setText(getResources().getString(R.string.bt_cmd_sending_error));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
                    break;

                ///////////////////////////////////////////////
                // When there's incoming packets on bluetooth
                // do the UI works like below
                ///////////////////////////////////////////////
                case Constants.MESSAGE_READ_CHAT_DATA:
                    if(msg.obj != null) {
                        ExampleFragment frg = (ExampleFragment) mSectionsPagerAdapter.getItem(FragmentAdapter.FRAGMENT_POS_EXAMPLE);
                        TestFragment tfrg = (TestFragment) mSectionsPagerAdapter.getItem(FragmentAdapter.FRAGMENT_POS_EXAMPLE);
                        frg.showMessage((String)msg.obj);
                    }
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }

    }	// End of class ActivityHandler


    public void OnFragmentCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4) {
        switch(msgType) {
            case IListener.CALLBACK_RUN_IN_BACKGROUND:
                if(mService != null)
                    mService.startServiceMonitoring();
                break;
            case IListener.CALLBACK_SEND_MESSAGE:
                if(mService != null && arg2 != null)
                    mService.sendMessageToRemote(arg2);

            default:
                break;
        }
    }

}
