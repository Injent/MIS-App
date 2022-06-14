package com.injent.miscalls.domain;

import static com.injent.miscalls.App.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.injent.miscalls.MainActivity;
import com.injent.miscalls.R;

public class ForegroundServiceApp extends Service {

    private static final int FOREGROUND_ID = 132005;

    @Override
    public void onCreate() {
        createNotificationChannel();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(getString(R.string.keyMoveToSettings),true);
        PendingIntent pendingIntent = new NavDeepLinkBuilder(getApplicationContext())
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.settingsFragment)
                .createPendingIntent();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getText(R.string.receivingDataOn))
                .setContentText(getText(R.string.moveToSettings))
                .setSmallIcon(R.drawable.ic_medical_suitcase)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(FOREGROUND_ID,notification);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.managerName),
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
