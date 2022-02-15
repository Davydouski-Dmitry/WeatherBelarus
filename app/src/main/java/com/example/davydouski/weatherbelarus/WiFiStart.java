package com.example.davydouski.weatherbelarus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class WiFiStart extends Activity{

    private Switch wifiSwitch;
    private WifiManager wifiManager;
    private Button butt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);


        Button checkBrn = (Button)findViewById(R.id.check);
         checkBrn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                if(isConnected())
                    Toast.makeText(getApplicationContext(),"INTERNET доступен", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(),"INTERNET не доступен", Toast.LENGTH_LONG).show();
             }
         });


        wifiSwitch = (Switch)findViewById(R.id.wifi_switch);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        butt = (Button)findViewById(R.id.butnHW5);

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean isChecked = true;
                if (isChecked){
                    wifiManager.setWifiEnabled(true);
                    wifiSwitch.setText("WIFI is ON");
                } else {
                    wifiManager.setWifiEnabled(false);
                    wifiSwitch.setText("WIFI is OFF");
                }
            }
        });

        if (wifiManager.isWifiEnabled()){
            wifiSwitch.setChecked(true);
            wifiSwitch.setText("WIFI is ON");
        } else {
            wifiSwitch.setChecked(false);
            wifiSwitch.setText("WIFI is OFF");
        }


        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte = new Intent(WiFiStart.this,MainActivity.class);
                startActivity(inte);
            }
        });


    }
     //для интернета
    private boolean isConnected() {
        ConnectivityManager connectivityManager =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo!=null){
            if(networkInfo.isConnected())
                return  true;
            else
                return false;
        }else
            return false;

    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra){
                case WifiManager.WIFI_STATE_ENABLED:
                    wifiSwitch.setChecked(true);
                    wifiSwitch.setText("WIFI is ON");
                    break;

                case WifiManager.WIFI_STATE_DISABLED:
                    wifiSwitch.setChecked(false);
                    wifiSwitch.setText("WIFI is OFF");
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiReceiver);
    }
}
