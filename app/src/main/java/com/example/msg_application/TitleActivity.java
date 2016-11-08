package com.example.msg_application;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.client.android.integration.IntentIntegrator;
import com.google.zxing.client.android.integration.IntentResult;

/**
 * Created by 욱 on 2016-10-14.
 */

public class TitleActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView req1 = (TextView) findViewById(R.id.requesttxt1 );
        TextView req2 = (TextView) findViewById(R.id.requesttxt2 );
        TextView bt = (TextView) findViewById(R.id.nextbutton1);


        bt.setBackgroundColor(Color.rgb(12,111,255));
        bt.setText("임시 버튼.");



        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                    Intent intent = new Intent(getApplicationContext() , SubtitleActivity.class);
                    intent.putExtra("name","넘어갈 자료");
                    startActivity(intent);
            }
        });

    }
}
