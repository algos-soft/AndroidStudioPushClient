package com.ats.pushclient;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by alex on 15/04/16.
 */
public class PDGcmListenerService extends GcmListenerService {

    /**
     * Chiamato quando si riceve il messaggio PUSH
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String messagetype=data.getString("messagetype");
        String details = data.getString("details");

        Log.d("GCMRECEIVE", "From: " + from + ", type: " + messagetype + " msg: " + details);

        Bundle b = new Bundle();
        b.putString("message",details);
        PushClientApp.createNotification(b);

    }

}









