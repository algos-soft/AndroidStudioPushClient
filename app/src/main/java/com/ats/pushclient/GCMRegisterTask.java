package com.ats.pushclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registra il dispositivo presso il server GCM in modo asincrono.
 */
public class GCMRegisterTask extends AsyncTask<Void, Void, String> {

    private MainActivity activity;

    public GCMRegisterTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    /**
     * Esegue la registrazione GCM, invia i dati al nostro server e
     * registra il token nei settings.
     * @return se qualcosa va storto ritorna una stringa di errore,
     * altrimenti stringa vuota se OK
     */
    protected String doInBackground(Void... params) {
        String err = "";
        Context ctx = PushClientApp.getContext();
        InstanceID instanceID = InstanceID.getInstance(ctx);
        String senderId = PushClientApp.getSenderId();
        try {

            // ottiene il token da GCM
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            // invio il token al nostro server
            sendTokenToServer(token);

            // registro il token nei settings
            PushClientApp.setToken(token);

        } catch (IOException e) {
            err = e.getMessage();
        }
        return err;
    }


    /**
     * Invio il token e i dati identificativi del device al nostro server
     */
    private void sendTokenToServer(String token) throws IOException {

        // ottengo il ConnectivityManager
        Context ctx = PushClientApp.getContext();
        ConnectivityManager mgr = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        // recupero le info sul network attivo
        NetworkInfo info=mgr.getActiveNetworkInfo();

        if(info!=null){
            if(info.isConnected()){

                String name=PushClientApp.getName();
                String model = Build.MODEL;
                String deviceId= Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

                String addr=PushClientApp.getRegisterAddress();
                URL url = new URL(addr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HashMap<String, String> params= new HashMap();
                params.put("action", "R");
                params.put("name", name);
                params.put("model", model);
                params.put("android_id", deviceId);
                params.put("gcm_token", token);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String sparams=PushClientApp.getQuery(params);
                writer.write(sparams);
                writer.flush();
                writer.close();
                os.close();

                // inizia la richiesta
                conn.connect();
                int response = conn.getResponseCode();
                if(response!=200){
                    throw new IOException("Server response: "+response);
                }

            }else{
                throw new IOException("Rete non connessa");
            }
        }else{
            throw new IOException("Nessun network attivo");
        }

    }



    @Override
    protected void onPostExecute(String err) {
        if(err.isEmpty()){  // è andata bene
            String s = "Registrazione GCM effettuata correttamente";
            Toast toast = Toast.makeText(activity, s, Toast.LENGTH_LONG);
            toast.show();

        }else{  // errore
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Registrazione GCM fallita");
            builder.setMessage(err);
            builder.setPositiveButton("Continua", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }

        activity.syncUI();

    }


}
