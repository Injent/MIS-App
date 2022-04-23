package com.injent.miscalls.domain;

import static com.injent.miscalls.App.CHANNEL_ID;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.injent.miscalls.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ForegroundServiceApp extends Service {

    private boolean init = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, ForegroundServiceApp.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getText(R.string.receivingDataOn))
                .setContentText(getText(R.string.moveToSettings))
                .setSmallIcon(R.drawable.ic_notif_on)
                .setContentIntent(pendingIntent)
                .build();

        init = true;
        Log.e("FOREGROUNDSERVICE","START");

        startForeground(1,notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public ForegroundServiceApp getService() {
            return ForegroundServiceApp.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    public boolean isRunning() {
        return init;
    }
}
