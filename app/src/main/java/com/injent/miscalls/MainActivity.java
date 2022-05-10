package com.injent.miscalls;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import com.injent.miscalls.domain.ForegroundServiceApp;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static WeakReference<MainActivity> instance;
    private ForegroundServiceApp foregroundServiceApp;

    public static MainActivity getInstance() {
        return instance.get();
    }

    private static void setInstance(MainActivity referent) {
        instance = new WeakReference<>(referent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInstance(this);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.statusBar, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar, getTheme()));

        if (App.getUserSettings().getMode() == 1) {
            createNotificationChannel();
            onForegroundService();
        } else
            stopService();

    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                App.CHANNEL_ID,
                getString(R.string.managerName),
                NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    public void confirmExit() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.exit)
                .setPositiveButton(R.string.yes, (dialog, button0) -> closeApp())
                .setNegativeButton(R.string.no, (dialog, button1) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    public void closeApp() {
        finish();
        System.exit(0);
    }

    public void startService() {
        Intent service = new Intent(this, ForegroundServiceApp.class);
        startForegroundService(service);
    }

    public void stopService() {
        Intent service = new Intent(this, ForegroundServiceApp.class);
        stopService(service);
    }

    public void onForegroundService() {
        ServiceConnection connection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                ForegroundServiceApp.LocalBinder binder = (ForegroundServiceApp.LocalBinder) service;
                foregroundServiceApp = binder.getService();

                if(!foregroundServiceApp.isRunning())
                    startService();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                //Nothing to do
            }
        };

        Intent intent = new Intent(this, ForegroundServiceApp.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
}