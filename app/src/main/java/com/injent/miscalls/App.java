package com.injent.miscalls;

import android.app.Application;

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
}
