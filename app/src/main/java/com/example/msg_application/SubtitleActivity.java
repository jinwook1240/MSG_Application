package com.example.msg_application;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msg_application.subtitle.service.BTCTemplateService;
import com.example.msg_application.subtitle.utils.AppSettings;
import com.example.msg_application.subtitle.utils.Constants;
import com.google.zxing.client.android.integration.IntentIntegrator;
import com.google.zxing.client.android.integration.IntentResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 욱 on 2016-11-02.
 */

public class SubtitleActivity extends Activity
{
    final int version = Build.VERSION.SDK_INT;
    static TextView req1;
    TextView req2;
    TextView bt;
    private ImageView mImageBT;
    String scaninfo;
    private Context mContext;
    private ActivityHandler mActivityHandler;
    private FragmentManager mFragmentManager;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subtitle_main);
        mContext = this;	//.getApplicationContext();
        AppSettings.initializeAppSettings(mContext);


        Intent in = getIntent();
        Bundle extras = in.getExtras();
        scaninfo = extras.getString("scaninfo");

        req1 = (TextView) findViewById(R.id.bluetoothtxt1 );
        req1.setText(scaninfo);
        req2 = (TextView) findViewById(R.id.bluetoothtxt2 );
        bt = (TextView) findViewById(R.id.bluetoothbtn1);
        mImageBT = (ImageView) findViewById(R.id.status_title);
        final Thread sender = new Timethread("subtitle");
        bt.setBackgroundColor(Color.BLUE);
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(sender.isAlive()) {
                    sender.interrupt();
                }else{
                    sender.start();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                // Launch the DeviceListActivity to see devices and do scan
                doScan();
                return true;
            case R.id.action_discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }




    private void doScan() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CONNECT_DEVICE);
    }

    private void ensureDiscoverable() {
        if (mService.getBluetoothScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }


    final Handler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<SubtitleActivity> mActivity;

        public MyHandler(SubtitleActivity activity) {
            mActivity = new WeakReference<SubtitleActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SubtitleActivity activity = mActivity.get();
            if (activity != null) {
                req1.setText((String)msg.obj);
            }
        }
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
                handler.sendEmptyMessage(1);

                Message message= Message.obtain();
                message.obj = messagem.toString();
                handler.sendMessage(message);
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


    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what) {
                // Receives BT state messages from service
                // and updates BT state UI
                case Constants.MESSAGE_BT_STATE_INITIALIZED:
                    req2.setText(/*getResources().getString(R.string.bt_title)*/"MSG 상태" + ": " +
                            getResources().getString(R.string.bt_state_init));
                    mImageBT.setImageDrawable(getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_LISTENING:
                    req2.setText(/*getResources().getString(R.string.bt_title)*/"MSG 상태" + ": " +
                            getResources().getString(R.string.bt_state_wait));
                    mImageBT.setImageDrawable(getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTING:
                    req2.setText(/*getResources().getString(R.string.bt_title)*/"MSG 상태" + ": " +
                            getResources().getString(R.string.bt_state_connect));
                    mImageBT.setImageDrawable(getDrawable(android.R.drawable.presence_away));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTED:
                    if(mService != null) {
                        String deviceName = mService.getDeviceName();
                        if(deviceName != null) {
                            req2.setText(/*getResources().getString(R.string.bt_title)*/"MSG 상태" + ": " +
                                    getResources().getString(R.string.bt_state_connected) + " " + deviceName);
                            mImageBT.setImageDrawable(getDrawable(android.R.drawable.presence_online));
                        }
                    }
                    break;
                case Constants.MESSAGE_BT_STATE_ERROR:
                    req2.setText(getResources().getString(R.string.bt_state_error));
                    mImageBT.setImageDrawable(getDrawable(android.R.drawable.presence_busy));
                    break;

                // BT Command status
                case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:
                    req2.setText(getResources().getString(R.string.bt_cmd_sending_error));
                    mImageBT.setImageDrawable(getDrawable(android.R.drawable.presence_busy));
                    break;

                ///////////////////////////////////////////////
                // When there's incoming packets on bluetooth
                // do the UI works like below
                ///////////////////////////////////////////////
                case Constants.MESSAGE_READ_CHAT_DATA:
                    if(msg.obj != null) {/*
                        SubtitleActivity sub = (SubtitleActivity) mSectionsPagerAdapter.getItem(FragmentAdapter.FRAGMENT_POS_EXAMPLE);
                        TestFragment tfrg = (TestFragment) mSectionsPagerAdapter.getItem(FragmentAdapter.FRAGMENT_POS_EXAMPLE);
                        frg.showMessage((String)msg.obj);*/
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
