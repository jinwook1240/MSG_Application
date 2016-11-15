package com.example.msg_application;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.msg_application.subtitle.utils.Constants;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.client.android.integration.IntentIntegrator;
import com.google.zxing.client.android.integration.IntentResult;

public class MainActivity extends Activity {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    boolean ifBarcodeCaptured=false;
    private GoogleApiClient client;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this, SplashActivity.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setSupportActionBar(toolbar);

        TextView bt = (TextView) findViewById(R.id.nextbutton1);
        AppCompatButton tb = (AppCompatButton) findViewById(R.id.Toolbarbutton);
        tb.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivityForResult(new Intent(getApplicationContext(), DeviceListActivity.class), Constants.REQUEST_CONNECT_DEVICE);
            }
        });
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(ifBarcodeCaptured){
                    Intent intent = new Intent(getApplicationContext() , TitleActivity.class);
                    intent.putExtra("scaninfo",result);
                    startActivity(intent);
                }
            }
        }
        );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator.initiateScan(MainActivity.this);

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView req1 = (TextView) findViewById(R.id.requesttxt1 );
        TextView req2 = (TextView) findViewById(R.id.requesttxt2 );
        TextView bt = (TextView) findViewById(R.id.nextbutton1);
        // QR코드/바코드를 스캔한 결과 값을 가져옵니다.
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result.getContents()!=null){
        bt.setBackgroundColor(Color.rgb(12,111,255));
        bt.setText("정보가 맞습니다\n다음 단계로 진행합니다.");
        req1.setText("스캔 완료!");
        req1.setTextColor(Color.MAGENTA);
        req2.setText("영화 정보가 맞는지 확인해주세요");
        // 결과값 출력
        ifBarcodeCaptured=true;
        this.result = result.getContents();
        Snackbar.make(toolbar, result.getContents()+"\n\n", Snackbar.LENGTH_INDEFINITE )
                .setAction("Action", null).show();
        }else{
            Snackbar.make(toolbar, "스캔 정보가 없습니다.\n\n", Snackbar.LENGTH_INDEFINITE )
                    .setAction("Action", null).show();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
