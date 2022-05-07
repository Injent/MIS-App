package com.injent.miscalls;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.injent.miscalls.data.User;
import com.injent.miscalls.data.calllist.CallInfoDao;
import com.injent.miscalls.data.AppDatabase;
import com.injent.miscalls.data.diagnosis.Diagnosis;
import com.injent.miscalls.data.diagnosis.DiagnosisDao;
import com.injent.miscalls.data.registry.RegistryDao;
import com.injent.miscalls.data.recommendation.RecommendationDao;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;

public class App extends Application {

    private static WeakReference<App> instance;

    public static final String APP_VERSION = "snapshot 22w18b";
    public static final String PREFERENCES_NAME = "settings";
    public static final String ENCRYPTED_PREFERENCES_NAME = "security-data";
    public static final String CHANNEL_ID = "service-v1";

    private AppDatabase appDatabase;
    private DiagnosisDao diagnosisDao;
    private RegistryDao registryDao;
    private RecommendationDao recommendationDao;
    private CallInfoDao callInfoDao;

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

        appDatabase = AppDatabase.getInstance(this);

        diagnosisDao = appDatabase.diagnosisDao();
        registryDao = appDatabase.registryDao();
        recommendationDao = appDatabase.recommendationDao();
        callInfoDao = appDatabase.callInfoDao();

        initSettings();
        initEncryptedPreferences();
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    public DiagnosisDao getDiagnosisDao() {
        return diagnosisDao;
    }

    public RegistryDao getRegistryDao() {
        return registryDao;
    }

    public RecommendationDao getRecommendationDao() {
        return recommendationDao;
    }

    public CallInfoDao getCallInfoDao() {
        return callInfoDao;
    }

    public static User getUser() { return user; }

    public static void setUser(User user) { App.user = user; }

    public boolean isAuthed() { return authed; }

    public void setAuthed(boolean authed) {
        this.authed = authed;
        SharedPreferences sp = getSharedPreferences();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("authed", authed);
        editor.apply();
    }

    public int getMode() { return mode; }

    public static void setMode(int mode) { App.mode = mode; }

    public SharedPreferences getSharedPreferences() { return getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE); }
    
    public SharedPreferences getEncryptedPreferences() {
        SharedPreferences esp = null;
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            esp = EncryptedSharedPreferences.create(
                    ENCRYPTED_PREFERENCES_NAME,
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return esp;
    }

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
        setMode(sp.getInt("mode", 1));
    }

    private void initEncryptedPreferences() {
        SharedPreferences esp = getEncryptedPreferences();
            
        User newUser = new User(
                esp.getString(getString(R.string.keyLogin),null),
                esp.getString(getString(R.string.keyPassword), null),
                esp.getString(getString(R.string.keyFirstName), null),
                esp.getString(getString(R.string.keyLastname), null),
                esp.getString(getString(R.string.keyMiddleName), null),
                esp.getString(getString(R.string.keyWorkingPosition), null),
                esp.getString(getString(R.string.keyToken), null)
        );
        setUser(newUser);
    }

    @SuppressLint("CommitPrefEdits")
    public void writeEncryptedData(User user) {
        setUser(user);
        SharedPreferences esp = getEncryptedPreferences();
        SharedPreferences.Editor editor = esp.edit();
        editor.putString(getString(R.string.keyLogin), user.getLogin());
        editor.putString(getString(R.string.keyPassword), user.getPassword());
        editor.putString(getString(R.string.keyWorkingPosition), user.getWorkingPosition());
        editor.putString(getString(R.string.keyToken), user.getQueryToken().getToken());
        editor.putString(getString(R.string.keyFirstName), user.getName());
        editor.putString(getString(R.string.keyLastname), user.getLastName());
        editor.putString(getString(R.string.keyMiddleName), user.getMiddleName());
        editor.apply();
        SharedPreferences sp = getSharedPreferences();
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putBoolean("authed", true);
        spEditor.apply();
        setAuthed(true);
    }

    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
