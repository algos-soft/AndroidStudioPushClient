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

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Button bSettings = (Button)findViewById(R.id.bcontinua);
        bSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i = getIntent();
        Bundle b = i.getBundleExtra("bundle");
        String msg = b.getString("message");
        TextView view = (TextView)findViewById(R.id.msg_placeholder);
        view.setText(msg);


    }

}
