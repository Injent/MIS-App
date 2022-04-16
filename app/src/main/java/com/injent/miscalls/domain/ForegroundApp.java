package com.injent.miscalls.domain;

import static com.injent.miscalls.App.CHANNEL_ID;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.injent.miscalls.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ForegroundApp extends Service {

    ExecutorService es;

    @Override
    public void onCreate() {
        super.onCreate();
        es = Executors.newFixedThreadPool(1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, ForegroundApp.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle(getText(R.string.receivingDataOn))
                        .setContentText(getText(R.string.moveToSettings))
                        .setSmallIcon(R.drawable.ic_notif_on)
                        .setContentIntent(pendingIntent)
                        .build();

        startForeground(1,notification);
        Run run = new Run(intent.getIntExtra("time", 1),startId);
        es.execute(run);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class Run implements Runnable {

        int time;
        int startId;

        public Run(int time, int startId) {
            this.time = time;
            this.startId = startId;
        }

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stopSelf(startId);
        }
    }
}
