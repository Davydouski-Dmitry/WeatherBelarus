package com.example.davydouski.weatherbelarus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

    public static final String BUTTON_KEY_NAME = "BUTTON_KEY_NAME";

    public static void show(Activity activity, int key) {
        Intent intent = new Intent(activity, StartActivity.class);
        intent.putExtra(BUTTON_KEY_NAME, key);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        int buttonKey = getIntent().getIntExtra(BUTTON_KEY_NAME, 0);

        Button butnClickHW1 = (Button) findViewById(R.id.buttonHW1);
        butnClickHW1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(StartActivity.this,WiFiStart.class);
                startActivity(intent);
            }
        });




        Button butnClickHW2 = (Button) findViewById(R.id.buttonHW2);
        butnClickHW2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(StartActivity.this,InformationActivity.class);
                startActivity(intent);
            }
        });
    }
}
