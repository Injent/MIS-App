package com.injent.miscalls.domain;

import android.content.Context;
import android.content.Intent;

import com.injent.miscalls.App;
import com.injent.miscalls.MainActivity;

public class BroadcastReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, ForegroundServiceApp.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
