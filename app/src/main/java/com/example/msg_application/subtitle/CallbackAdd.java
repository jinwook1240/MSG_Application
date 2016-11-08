package com.example.msg_application.subtitle;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.msg_application.IListener;
import com.example.msg_application.subtitle.service.BTCTemplateService;
import com.example.msg_application.subtitle.utils.Constants;
import com.example.msg_application.subtitle.utils.Logs;

import static android.content.ContentValues.TAG;

/**
 * Created by CLP on 2016-11-09.
 */

public class CallbackAdd {
    BTCTemplateService mService;
    mActivityHandler = new ActivityHandler();


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
    private ServiceConnection mServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "Activity - Service connected");

            mService = ((BTCTemplateService.ServiceBinder) binder).getService();

            // Activity couldn't work with mService until connections are made
            // So initialize parameters and settings here. Do not initialize while running onCreate()
            initialize();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    private void initialize() {
        Logs.d(TAG, "# Activity - initialize()");
        mService.setupService(mActivityHandler);

        // If BT is not on, request that it be enabled.
        // RetroWatchService.setupBT() will then be called during onActivityResult
        if(!mService.isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }

        // Load activity reports and display
        if(mRefreshTimer != null) {
            mRefreshTimer.cancel();
        }

        // Use below timer if you want scheduled job
        //mRefreshTimer = new Timer();
        //mRefreshTimer.schedule(new RefreshTimerTask(), 5*1000);
    }
}
