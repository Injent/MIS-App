package com.injent.miscalls.domain;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BackgroundServiceApp extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("BACKGROUNDSERVICE","START");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.d("Background", "Service is running");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
