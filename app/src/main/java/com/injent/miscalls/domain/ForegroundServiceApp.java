package com.injent.miscalls.domain;

import static com.injent.miscalls.App.CHANNEL_ID;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.injent.miscalls.App;
import com.injent.miscalls.R;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ForegroundServiceApp extends Service {

    private boolean init = false;
    private static final int FOREGROUND_ID = 1;
    public static final long UPDATE_TIME = 5L;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ScheduledExecutorService es = new ScheduledThreadPoolExecutor(1);
        es.scheduleAtFixedRate(() -> App.getInstance().getSharedPreferences().edit().putBoolean("newDb", true).apply(),0, UPDATE_TIME, TimeUnit.SECONDS);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getText(R.string.receivingDataOn))
                .setContentText(getText(R.string.moveToSettings))
                .setSmallIcon(R.drawable.ic_notif_on)
                .build();

        init = true;
        Log.e("FOREGROUNDING","START");

        startForeground(FOREGROUND_ID,notification);
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
