package com.injent.miscalls;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.injent.miscalls.data.AuthModelIn;
import com.injent.miscalls.data.patientlist.PatientDao;
import com.injent.miscalls.data.patientlist.PatientDatabase;

import java.lang.ref.WeakReference;

public class App extends Application {

    public static final String PREFERENCES_NAME = "settings";
    private static WeakReference<App> instance;
    private PatientDatabase pdb;
    private PatientDao patientDao;
    private boolean initialized;
    private AuthModelIn user;
    private boolean signed;
    private boolean autoUpdate;

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

        patientDao = pdb.patientDao();
        //

        SharedPreferences sp = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (sp.contains("init")) {
            editor.putBoolean("autoUpdate", true);
            editor.putBoolean("auth", false);
            editor.putString("personalLink","");
        }
        autoUpdate = sp.getBoolean("autoUpdate", true);
    }

    public AuthModelIn getUser() {
        return user;
    }

    public void setUser(AuthModelIn model) {
        this.user = model;
    }

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

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) { this.autoUpdate = autoUpdate; }
}
