package com.ats.pushclient;

import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by alex on 15/04/16.
 */
public class PDInstanceIdListenerService extends InstanceIDListenerService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        Log.d("GCM","Token refresh received");
    }

}
