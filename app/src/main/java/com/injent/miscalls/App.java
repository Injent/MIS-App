package com.injent.miscalls;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;

import androidx.room.Room;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.injent.miscalls.data.User;
import com.injent.miscalls.data.calllist.CallDatabase;
import com.injent.miscalls.data.calllist.CallInfoDao;
import com.injent.miscalls.data.savedprotocols.InspectionDao;
import com.injent.miscalls.data.savedprotocols.InspectionDatabase;
import com.injent.miscalls.data.diagnosis.ProtocolTempDao;
import com.injent.miscalls.data.diagnosis.RecommendationsDatabase;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;

public class App extends Application {

    private static WeakReference<App> instance;
    public static final String APP_VERSION = "22w18a";

    public static final String PREFERENCES_NAME = "settings";
    public static final String ENCRYPTED_PREFERENCES_NAME = "security-data";
    public static final String CHANNEL_ID = "service-v1";
    private CallDatabase callDatabase;
    private CallInfoDao callInfoDao;
    private ProtocolTempDao recommendationTempDao;
    private RecommendationsDatabase recommendationsDatabase;
    private InspectionDao inspectionDao;
    private InspectionDatabase inspectionDatabase;
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

        final char[] userEnteredPassphrase = new char[]{'a','b'};
        final byte[] passphrase = SQLiteDatabase.getBytes(userEnteredPassphrase);
        final SupportFactory factory = new SupportFactory(passphrase);

        callDatabase = Room.databaseBuilder(this, CallDatabase.class, CallDatabase.DB_NAME)
                .openHelperFactory(factory)
                .build();

        recommendationsDatabase = Room.databaseBuilder(this, RecommendationsDatabase.class, RecommendationsDatabase.DB_NAME)
                .openHelperFactory(factory)
                .build();

        inspectionDatabase = Room.databaseBuilder(this, InspectionDatabase.class, InspectionDatabase.DB_NAME)
                .openHelperFactory(factory)
                .build();

        callInfoDao = callDatabase.patientDao();
        recommendationTempDao = recommendationsDatabase.protocolDao();
        inspectionDao = inspectionDatabase.protocolDao();

        initSettings();
        initEncryptedPreferences();
    }

    public InspectionDao getProtocolDao() { return inspectionDao; }

    public InspectionDatabase getProtocolDatabase() { return inspectionDatabase; }

    public ProtocolTempDao getRecommendationTempDao() { return recommendationTempDao; }

    public RecommendationsDatabase getProtocolTempDatabase() { return recommendationsDatabase; }

    public static User getUser() { return user; }

    public static void setUser(User user) { App.user = user; }

    public CallDatabase getCallDatabase() { return callDatabase; }

    public CallInfoDao getPatientDao() { return callInfoDao; }

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
}
