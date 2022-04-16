package com.injent.miscalls;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.injent.miscalls.data.AuthModelIn;
import com.injent.miscalls.data.patientlist.PatientDao;
import com.injent.miscalls.data.patientlist.PatientDatabase;
import com.injent.miscalls.data.User;
import com.injent.miscalls.data.templates.ProtocolDao;
import com.injent.miscalls.data.templates.ProtocolDatabase;
import com.injent.miscalls.domain.AuthRepository;
import com.injent.miscalls.domain.ForegroundApp;

import java.lang.ref.WeakReference;

public class App extends Application {

    private static WeakReference<App> instance;

    public static final int FOREGROUND_TIME = 20;
    public static final String PREFERENCES_NAME = "settings";
    public static final String CHANNEL_ID = "service-v1";
    private PatientDatabase pdb;
    private PatientDao patientDao;
    private ProtocolDao protocolDao;
    private ProtocolDatabase protocolDatabase;
    private boolean initialized;
    private boolean signed;
    private static int mode;
    private static User user;

    public static App getInstance() {
        return instance.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initialized = false;

        instance = new WeakReference<>(this);

        pdb = Room.databaseBuilder(getApplicationContext(),PatientDatabase.class,PatientDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build();

        protocolDatabase = Room.databaseBuilder(getApplicationContext(), ProtocolDatabase.class,
                ProtocolDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build();

        patientDao = pdb.patientDao();
        protocolDao = protocolDatabase.protocolDao();

        SharedPreferences sp = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (sp.contains("init")) {
            editor.putInt("mode", 1);
            editor.putBoolean("auth", false);
            editor.putString("personalLink","");
            editor.putInt("section",0);
            editor.apply();
        }
        //Переделать в DB TODO
        user = new User("user1","12345","Имя", "Фамилия","Отчество","Доктор","qedfqeofnqifnqnflqwkjfqwlkfbqwkljfbqwfjklbqwf");
        mode = sp.getInt("mode", 1);

        if (mode == 1) {
            createNotificationChannel();
            startService();
        } else {
            stopService();
        }
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

    public ProtocolDatabase getProtocolDatabase() {
        return protocolDatabase;
    }

    public void setProtocolDatabase(ProtocolDatabase protocolDatabase) {
        this.protocolDatabase = protocolDatabase;
    }

    public User getUser() { return user; }

    public void setUser(User user) { App.user = user; }

    public PatientDatabase getPdb() { return pdb; }

    public PatientDao getPatientDao() { return patientDao; }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isSigned() { return signed; }

    public void setSigned(boolean signed) { this.signed = signed; }

    public int getMode() { return mode; }

    public void setMode(int mode) { App.mode = mode; }

    public void startService() {
        Log.i("App", "FOREGROUND SERVICE STARTED");
        Intent service = new Intent(this, ForegroundApp.class);
        service.putExtra("time",FOREGROUND_TIME);
        ContextCompat.startForegroundService(this, service);
    }

    public void stopService() {
        Log.i("App", "FOREGROUND SERVICE STOPPED");
        Intent service = new Intent(this, ForegroundApp.class);
        stopService(service);
    }
}
