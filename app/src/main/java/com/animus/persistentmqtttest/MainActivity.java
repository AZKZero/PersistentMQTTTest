package com.animus.persistentmqtttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = findViewById(R.id.button),
                stop = findViewById(R.id.stop);

        if(ConnectionManager.mqat!=null) {
            if(ConnectionManager.mqat.isConnected()) {
                start.setText("CONNECTED");
                ConnectionManager.subscribe("test");
            }
        }


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(getApplicationContext(), NotificationService.class));
                } else {
                    startService(new Intent(getApplicationContext(), NotificationService.class));
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), NotificationService.class));
            }
        });

        registerReceiver(broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Do Some Stuff
            }
        }, new IntentFilter("YOUR_DATA_FILTER"));
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver != null) {
            registerReceiver(broadcastReceiver, new IntentFilter("YOUR_DATA_FILTER"));
        } else {
            registerReceiver(broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //Do Some Stuff
                }
            }, new IntentFilter("YOUR_DATA_FILTER"));
        }
    }
}
