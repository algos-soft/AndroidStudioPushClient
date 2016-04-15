package com.ats.pushclient;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ATS on 14/04/2016.
 */
public class PushClientApp extends Application{

    private static PushClientApp instance;
    private static final String KEY_REGISTER_ADDR="registeraddr";
    private static final String KEY_TOKEN="token";
    private static final String KEY_SENDER_ID="senderid";
    private static final String KEY_NAME="name";

    public PushClientApp() {
        instance=this;
    }

    public static PushClientApp getInstance() {
        return instance;
    }

    /**
     * Restituisce l'indirizzo di registrazione sul nostro server
     */
    public static String getRegisterAddress(){
        return instance.getPrefs().getString(KEY_REGISTER_ADDR,null);
    }

    /**
     * Registra l'indirizzo di registrazione sul nostro server
     */
    public static void setRegisterAddress(String addr){
        SharedPreferences.Editor editor= instance.getPrefs().edit();
        editor.putString(KEY_REGISTER_ADDR, addr);
        editor.commit();
    }

    /**
     * Restituisce il token GCM
     */
    public static String getToken(){
        return instance.getPrefs().getString(KEY_TOKEN,null);
    }

    /**
     * Registra il token GCM
     */
    public static void setToken(String token){
        SharedPreferences.Editor editor= instance.getPrefs().edit();
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    /**
     * Restituisce la sender id
     */
    public static String getSenderId(){
        return instance.getPrefs().getString(KEY_SENDER_ID,"115110381874");
    }

    /**
     * Registra la sender id
     */
    public static void setSenderId(String stringa){
        SharedPreferences.Editor editor= instance.getPrefs().edit();
        editor.putString(KEY_SENDER_ID, stringa);
        editor.commit();
    }


    /**
     * Restituisce il nome del device
     */
    public static String getName(){
        return instance.getPrefs().getString(KEY_NAME,null);
    }

    /**
     * Registra il nome del device
     */
    public static void setName(String stringa){
        SharedPreferences.Editor editor= instance.getPrefs().edit();
        editor.putString(KEY_NAME, stringa);
        editor.commit();
    }




    /**
     * Verifica se siamo registrati su GCM
     */
    public static boolean isRegistered(){
        boolean registered=false;
        String token = getToken();
        if(token !=null){
            if(!token.isEmpty()){
                registered=true;
            }
        }
        return registered;
    }

    /**
     * Ritorna l'oggetto SharedPreferences
     * che mantiene le prefernze dell'app
     */
    private SharedPreferences getPrefs(){
        return getSharedPreferences("prefs", MODE_PRIVATE);
    }

    public static Context getContext(){
        return instance;
    }

    /**
     * Crea una notifica di sistema
     */
    public static void createNotification(Bundle data){

        Intent intent = new Intent();
        intent.setClass(instance, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("bundle",data);

        PendingIntent pendingIntent = PendingIntent.getActivity(instance, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String msg = data.getString("message");
        Uri soudUri = resourceToUri(R.raw.surprise);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(instance);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Push notification");
        builder.setContentText(msg);
        builder.setAutoCancel(true);
        builder.setSound(soudUri);
        builder.setContentIntent(pendingIntent);

        Notification notif = builder.build();
        notif.flags |= Notification.FLAG_INSISTENT;
        NotificationManager manager=(NotificationManager)instance.getSystemService(NOTIFICATION_SERVICE);
        int notifId=1;  // id della notifica, notifiche con lo stesso id sostituiscono le precedenti
        manager.notify(notifId, notif);

        // dopo l'invio della notifica sveglio il device
        PowerManager pm = (PowerManager)instance.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(
                (PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP),
                instance.getClass().getSimpleName());
        wakelock.acquire();

    }


    private static Uri resourceToUri(int resId){
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+
                instance.getResources().getResourcePackageName(resId)+"/"+
                instance.getResources().getResourceTypeName(resId)+"/"+
                instance.getResources().getResourceEntryName(resId));
    }

    public static String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result=new StringBuilder();
        boolean first=true;
        for (Map.Entry entry : params.entrySet()){
            String sKey=(String)entry.getKey();
            String sValue=(String)entry.getValue();

            if(first){
                first=false;
            }else{
                result.append("&");
            }

            result.append(URLEncoder.encode(sKey, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(sValue,"UTF-8"));
        }

        return result.toString();
    }



}
