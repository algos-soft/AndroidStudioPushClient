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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Deregistra il dispositivo dal server GCM in modo asincrono.
 */
public class GCMUnregisterTask extends AsyncTask<Void, Void, String> {

    private MainActivity activity;

    public GCMUnregisterTask(MainActivity activity) {
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
        try {

            // deregistra il device dal nostro server
            unregisterFromServer();

            // deregistra il device da GCM
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(PushClientApp.getContext());
            gcm.unregister();

            // cancello il token nei settings
            PushClientApp.setToken(null);

        } catch (IOException e) {
            err = e.getMessage();
        }
        return err;
    }

    @Override
    protected void onPostExecute(String err) {
        if(err.isEmpty()){  // è andata bene
            String s = "GCM de-registrato";
            Toast toast = Toast.makeText(activity, s, Toast.LENGTH_LONG);
            toast.show();

        }else{  // errore
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Deregistrazione GCM fallita");
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

    /**
     * Deregistra il device dal nostro server
     */
    private void unregisterFromServer() throws IOException {

        // ottengo il ConnectivityManager
        Context ctx = PushClientApp.getContext();
        ConnectivityManager mgr = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        // recupero le info sul network attivo
        NetworkInfo info=mgr.getActiveNetworkInfo();

        if(info!=null){
            if(info.isConnected()){

                String deviceId=Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

                String addr=PushClientApp.getRegisterAddress();
                URL url = new URL(addr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HashMap<String, String> params= new HashMap();
                params.put("action", "U");
                params.put("android_id", deviceId);

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


}
