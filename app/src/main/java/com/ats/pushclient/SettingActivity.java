package com.ats.pushclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button bSave = (Button)findViewById(R.id.bsave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                finish();
            }
        });

        load();
    }


    /**
     * Dalle preferenze alla UI
     */
    private void load() {
        TextView tv;
        tv = (TextView)findViewById(R.id.token);
        tv.setText(PushClientApp.getToken());

        tv = (TextView)findViewById(R.id.registrationurl);
        tv.setText(PushClientApp.getRegisterAddress());

        tv = (TextView)findViewById(R.id.senderid);
        tv.setText(PushClientApp.getSenderId());

        tv = (TextView)findViewById(R.id.name);
        tv.setText(PushClientApp.getName());


    }

    /**
     * Dalla UI alle preferenze
     */
    private void save() {
        TextView tv;
        tv = (TextView)findViewById(R.id.token);
        PushClientApp.setToken(tv.getText().toString());

        tv = (TextView)findViewById(R.id.registrationurl);
        PushClientApp.setRegisterAddress(tv.getText().toString());

        tv = (TextView)findViewById(R.id.senderid);
        PushClientApp.setSenderId(tv.getText().toString());

        tv = (TextView)findViewById(R.id.name);
        PushClientApp.setName(tv.getText().toString());


    }



}
