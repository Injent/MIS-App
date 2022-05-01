package com.injent.miscalls;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.injent.miscalls.data.User;
import com.injent.miscalls.data.patientlist.PatientDao;
import com.injent.miscalls.data.patientlist.PatientDatabase;
import com.injent.miscalls.data.savedprotocols.ProtocolDao;
import com.injent.miscalls.data.savedprotocols.ProtocolDatabase;
import com.injent.miscalls.data.templates.ProtocolTempDao;
import com.injent.miscalls.data.templates.ProtocolTempDatabase;

import java.lang.ref.WeakReference;

public class App extends Application {

    private static WeakReference<App> instance;

    public static final String PREFERENCES_NAME = "settings";
    public static final String CHANNEL_ID = "service-v1";
    private PatientDatabase pdb;
    private PatientDao patientDao;
    private ProtocolTempDao protocolTempDao;
    private ProtocolTempDatabase protocolTempDatabase;
    private ProtocolDao protocolDao;
    private ProtocolDatabase protocolDatabase;
    private boolean authed;
    private static int mode;
    private static User user;

    public static App getInstance() {
        return instance.get();
    }

    private static void setInstance(App referent) {
        instance = new WeakReference<>(referent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setInstance(this);

        pdb = Room.databaseBuilder(this,PatientDatabase.class,PatientDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build();

        protocolTempDatabase = Room.databaseBuilder(this, ProtocolTempDatabase.class,
                ProtocolTempDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build();

        protocolDatabase = Room.databaseBuilder(this, ProtocolDatabase.class,
                ProtocolDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build();

        patientDao = pdb.patientDao();
        protocolTempDao = protocolTempDatabase.protocolDao();
        protocolDao = protocolDatabase.protocolDao();

        initSettings();
    }

    public ProtocolDao getProtocolDao() { return protocolDao; }

    public ProtocolDatabase getProtocolDatabase() { return protocolDatabase; }

    public ProtocolTempDao getProtocolTempDao() { return protocolTempDao; }

    public ProtocolTempDatabase getProtocolTempDatabase() { return protocolTempDatabase; }

    public static User getUser() { return user; }

    public static void setUser(User user) { App.user = user; }

    public PatientDatabase getPdb() { return pdb; }

    public PatientDao getPatientDao() { return patientDao; }

    public boolean isAuthed() { return authed; }

    public void setAuthed(boolean authed) { this.authed = authed; }

    public int getMode() { return mode; }

    public static void setMode(int mode) { App.mode = mode; }

    public SharedPreferences getSharedPreferences() { return getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE); }

    private void initSettings() {
        SharedPreferences sp = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (!sp.contains("init")) {
            editor.putInt("mode", 1);
            editor.putBoolean("authed", false);
            editor.putString("personalLink","");
            editor.putInt("section",0);
            editor.putBoolean("init", true);
            editor.apply();
        }
        authed = sp.getBoolean("authed", false);
//Временно TODO
        authed = false;
        mode = sp.getInt("mode", 1);
    }
}
