package com.example.davydouski.weatherbelarus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class WiFiStart extends Activity{

    private Switch wifiSwitch;
    private WifiManager wifiManager;
    private Button butt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

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
