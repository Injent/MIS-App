package com.injent.miscalls;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.injent.miscalls.data.User;
import com.injent.miscalls.data.UserSettings;
import com.injent.miscalls.data.database.AppDatabase;
import com.injent.miscalls.data.database.calls.CallInfoDao;
import com.injent.miscalls.data.database.diagnoses.DiagnosisDao;
import com.injent.miscalls.data.database.registry.RegistryDao;
import com.injent.miscalls.data.recommendation.RecommendationDao;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;

public class App extends Application {

    private static WeakReference<App> instance;

    public static final String PREFERENCES_NAME = "settings";
    public static final String ENCRYPTED_PREFERENCES_NAME = "security-data";
    public static final String CHANNEL_ID = "service-v1";

    private DiagnosisDao diagnosisDao;
    private RegistryDao registryDao;
    private RecommendationDao recommendationDao;
    private CallInfoDao callInfoDao;

    private static UserSettings userSettings;
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
        setUserSettings(new UserSettings(getResources(),getSharedPreferences()));

        initSettings();
        initEncryptedPreferences();
        connectDatabase();
    }

    private void connectDatabase() {
        if (userSettings.isAuthed()) {
            SharedPreferences esp = getEncryptedPreferences();
            String key = esp.getString(getString(R.string.keyToken),null);
            AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext(), key.toCharArray());

            diagnosisDao = appDatabase.diagnosisDao();
            registryDao = appDatabase.registryDao();
            recommendationDao = appDatabase.recommendationDao();
            callInfoDao = appDatabase.callInfoDao();
        }
    }

    public static UserSettings getUserSettings() {
        return userSettings;
    }

    private static void setUserSettings(UserSettings us) {
        userSettings = us;
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
        if (!userSettings.isInit()) {
            userSettings
                    .setMode(0)
                    .setAuthed(false)
                    .setAnonCall(false)
                    .setInit(true)
                    .write();
        }
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
        userSettings.setAuthed(true).write();
        connectDatabase();
    }

    public static void hideKeyBoard(View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
