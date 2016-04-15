package com.ats.pushclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bSettings = (Button)findViewById(R.id.bSettings);
        bSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(i);
            }
        });

        Button bRegister = (Button)findViewById(R.id.bRegister);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PushClientApp.isRegistered()){
                    unregister();
                }else{
                    register();
                }

            }
        });

        Button bTestNotifica = (Button)findViewById(R.id.bTestNotifica);
        bTestNotifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("message","descrizione dela notifica");
                PushClientApp.createNotification(b);
            }
        });


        syncUI();

    }


    public void syncUI(){
        Button bRegister = (Button)findViewById(R.id.bRegister);
        String status;
        if(PushClientApp.isRegistered()){
            bRegister.setText("Unregister from GCM");
            status="Registered";
        }else{
            bRegister.setText("Register to GCM");
            status="Not registered";
        }

        TextView statusView = (TextView)findViewById(R.id.status);
        statusView.setText(status);

    }


    /**
     * Registra il device su GCM
     */
    private void register() {
        GCMRegisterTask task = new GCMRegisterTask(this);
        task.execute();
    }

    /**
     * Deregistra il device
    */
    private void unregister() {
        GCMUnregisterTask task = new GCMUnregisterTask(this);
        task.execute();
    }


}
