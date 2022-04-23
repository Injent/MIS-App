package com.injent.miscalls;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.room.Room;

import com.injent.miscalls.data.User;
import com.injent.miscalls.data.patientlist.PatientDao;
import com.injent.miscalls.data.patientlist.PatientDatabase;
import com.injent.miscalls.data.templates.ProtocolDao;
import com.injent.miscalls.data.templates.ProtocolDatabase;
import com.injent.miscalls.domain.ForegroundServiceApp;

import java.lang.ref.WeakReference;

public class App extends Application {

    private static WeakReference<App> instance;

    public static final String PREFERENCES_NAME = "settings";
    public static final String CHANNEL_ID = "service-v1";
    private PatientDatabase pdb;
    private PatientDao patientDao;
    private ProtocolDao protocolDao;
    private ProtocolDatabase protocolDatabase;
    private boolean authed;
    private static int mode;
    private static User user;
    private ForegroundServiceApp foregroundServiceApp;

    public static App getInstance() {
        return instance.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = new WeakReference<>(this);

        pdb = Room.databaseBuilder(this,PatientDatabase.class,PatientDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build();

        protocolDatabase = Room.databaseBuilder(this, ProtocolDatabase.class,
                ProtocolDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build();

        patientDao = pdb.patientDao();
        protocolDao = protocolDatabase.protocolDao();

        initSettings();
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

    public ProtocolDao getProtocolDao() { return protocolDao; }

    public ProtocolDatabase getProtocolDatabase() { return protocolDatabase; }

    public User getUser() { return user; }

    public void setUser(User user) { App.user = user; }

    public PatientDatabase getPdb() { return pdb; }

    public PatientDao getPatientDao() { return patientDao; }

    public boolean isAuthed() { return authed; }

    public void setAuthed(boolean authed) { this.authed = authed; }

    public int getMode() { return mode; }

    public void setMode(int mode) { App.mode = mode; }

    public SharedPreferences getSharedPreferences() { return getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE); }

    public void startService() {
        Intent service = new Intent(this, ForegroundServiceApp.class);
        startForegroundService(service);
    }

    public void stopService() {
        Intent service = new Intent(this, ForegroundServiceApp.class);
        stopService(service);
    }

    private void onForegroundService() {
        ServiceConnection connection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                ForegroundServiceApp.LocalBinder binder = (ForegroundServiceApp.LocalBinder) service;
                foregroundServiceApp = binder.getService();

                // Calling your service public method
                if(foregroundServiceApp.isRunning()) {
                    // Your service is enabled
                } else {
                    startService();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {

            }
        };

        // Bind to MyService
        Intent intent = new Intent(this, ForegroundServiceApp.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void initSettings() {
        SharedPreferences sp = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (!sp.contains("init")) {
            editor.putInt("mode", 1);
            editor.putBoolean("authed", false);
            editor.putString("personalLink","");
            editor.putInt("section",0);
            editor.apply();
        }
        authed = sp.getBoolean("authed", false);
//Временно TODO
        authed = false;
        mode = sp.getInt("mode", 1);

        if (mode == 1) {
            createNotificationChannel();
            onForegroundService();
        } else stopService();
    }
}
